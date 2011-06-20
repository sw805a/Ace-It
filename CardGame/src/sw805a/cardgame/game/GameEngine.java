package sw805a.cardgame.game;

import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.media.MediaPlayer;

import sw805a.cardgame.R;
import sw805a.cardgame.comm.Client;
import sw805a.cardgame.comm.CommunicationType;
import sw805a.cardgame.comm.IGameCommunication;
import sw805a.cardgame.comm.bluetooth.BluetoothGameCommunication;
import sw805a.cardgame.comm.internet.InternetGameCommunication;
import sw805a.cardgame.comm.listeners.OnMessageListener;
import sw805a.cardgame.comm.listeners.OnPlayerConnectedListener;
import sw805a.cardgame.domain.Invitation;
import sw805a.cardgame.domain.InvitationAccepted;
import sw805a.cardgame.domain.ReadyForSync;
import sw805a.cardgame.domain.SyncDone;
import sw805a.cardgame.game.models.Card;
import sw805a.cardgame.game.models.GameState;
import sw805a.cardgame.game.models.Move;
import sw805a.cardgame.game.models.Pile;
import sw805a.cardgame.game.models.Player;

public class GameEngine implements IGameEngine {
	private static GameEngine _instance = new GameEngine();
	private static IRuleEngine _ruleEngine;
	public static IGameEngine getInstance(){
		return _instance;
	}
	
	private Client _disconnectedClient;
	private IGameCommunication _communicator;
	private ArrayList<Client> _clients = new ArrayList<Client>();
	private boolean _isHost = false;
	
	private GameEngine() {
		
	} 
	@Override
	public void setCommunicationType(CommunicationType type) {
		switch (type) {
			case BLUETOOTH:
				_communicator = new BluetoothGameCommunication();	
				break;
		
			case INTERNET:
				_communicator = new InternetGameCommunication();
				break;
		}
		_communicator.addOnPlayerConnected(new OnPlayerConnectedListener() {	
			@Override
			public void onConnect(Client client) {
				if(_isHost){
					Invitation msg = new Invitation(getClients());
					_communicator.sendMessage(client, msg);
				}
			}
		});
	}
	
	@Override
	public void answerInvite(boolean answer) {
		if(answer) {
			_communicator.sendMessage(new InvitationAccepted());
			clientInitializeGame();
		} else {
			_communicator.disconnect();
		}
	}
	
	
	
	@Override
	public void inviteClients() {
		ReadyForSync.getInstance().reset();
		ReadyForSync.getInstance().deleteObservers();
		ReadyForSync.getInstance().addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				if (ReadyForSync.getInstance().getReadyCount() == _clients.size()) {
					ReadyForSync.getInstance().deleteObservers();
					hostInitializeGame();
				}
			}
		});
		getCommunicator().removeMessageListeners(ReadyForSync.class);
		getCommunicator().addMessageListener(ReadyForSync.class, new OnMessageListener() {
			@Override
			public void OnMessage(Client sender, Object message) {
				ReadyForSync.getInstance().nextReady();
			}
		});
		
		_isHost = true;
		_communicator.setInviteeCount(_clients.size());
		for (Client target : _clients) {
			try {
				_communicator.invite(target);
			} catch (Exception e) {
				
			}
		}
	}



	@Override
	public ArrayList<Client> getClients() {
		return _clients;
	}
	
	@Override
	public void setDisconnectedClient(Client player) {
		_disconnectedClient = player;
	}
	
	@Override
	public Client getDisconnectedClient() {
		return _disconnectedClient;
	}

	@Override
	public IGameCommunication getCommunicator() {
		return _communicator;
	}

	public void setClients(ArrayList<Client> clients) {
		_clients.clear();
		for(Client p : clients) {
			_clients.add(new Client(p.getAddress(), p.getName()));
		}
	}

	@Override
	public boolean isHost() {
		return _isHost;
	}

	@Override
	public void hardReset() {
		if(_communicator != null){
			_communicator.leaveLobby();
		}
		_instance = new GameEngine();
		
	}
		
	

	@Override
	public void selectCard(Card card) {	
		getRuleEngine().selectCard(card);
	}


	@Override
	public boolean makeMove() {
		Move move = getRuleEngine().makeMove();
		if (move != null) {
			_communicator.sendMessage(move);
			return true;
		}
		return false;
	}

	@Override
	public void makePass() {
		makeMove();
	}
	
	@Override
	public IRuleEngine getRuleEngine() {
		return _ruleEngine;
	}

	private GameState _syncedGameState;
	
	private void clientInitializeGame() {
		_communicator.removeMessageListeners(SyncDone.class);
		_communicator.removeMessageListeners(GameState.class);
	
		_communicator.addMessageListener(SyncDone.class, new OnMessageListener() {			
			@Override
			public void OnMessage(Client sender, Object message) {
				Player me = null;
				ArrayList<Player> players = _syncedGameState.getBoard().getPlayers();
				for (Player player : players) {
					if (player.getAddress().equals(_communicator.getMyAddress())) {
						me = player;
						break;
					}
				}
				if (me == null) return;
				int myId = me.getId();
				for (Player player : players) {
					player.setId((players.size() + player.getId() - myId) % players.size());
				}
				
				Collections.sort(players, new Comparator<Player>() {
					@Override
					public int compare(Player object1, Player object2) {
						return object1.getId() - object2.getId();
					}
				});
				
				HashMap<Integer,Card> deck = _syncedGameState.getBoard().getDeck();
				for (Pile pile : _syncedGameState.getBoard().getPiles()) {
					for (Card card : pile.getCards()) {
						deck.put(card.getId(), card);
					}
				}
				for (Player player : _syncedGameState.getBoard().getPlayers()) {
					for (Card card : player.getHand().getCards()) {
                        deck.put(card.getId(), card);
					}
                }
				_syncedGameState.getBoard().setDeck(deck);
				
				getRuleEngine().receiveGameState(me, _syncedGameState);
				SyncDone.getInstance().synced();
			}
		});
			
		_communicator.addMessageListener(GameState.class, new OnMessageListener() {
			@Override
			public void OnMessage(Client sender, Object message) {
				GameState gs = (GameState) message;
				_syncedGameState = gs;
			}
		});
		_communicator.sendMessage(ReadyForSync.getInstance());
	
		initializeReceiveMove();
	}

	private void hostInitializeGame() {
		// Set the players
		Player me = new Player(_communicator.getMyAddress(), _communicator.getMyName());

		int i = 0;
		final ArrayList<Player> players = new ArrayList<Player>();
		players.add(0,me);
		me.setId(i++);
		for (Client c : _clients) {
			Player player = new Player(c.getAddress(), c.getName());
			player.setId(i++);
			players.add(player);
		}
		// deal cards
		getRuleEngine().initializeGame(me,players);
		
		
		// Send the state to the other players
		_communicator.sendMessage(getRuleEngine().getGameState());			
		_communicator.sendMessage(SyncDone.getInstance());
		initializeReceiveMove();
		SyncDone.getInstance().synced();
	}
	
	private void initializeReceiveMove() {
		_communicator.removeMessageListeners(Move.class);
		_communicator.addMessageListener(Move.class, new OnMessageListener() {
			@Override
			public void OnMessage(Client sender, Object message) {
				Move move = (Move) message;
				
				for (Client client : _clients) {
					if (!client.getAddress().equals(sender.getAddress())) {
						_communicator.sendMessage(client,move);
					}
				}
				
				getRuleEngine().recieveMove(move);
			}
		});
	}
	@Override
	public String[] getAvailableGames() {
		return new String[] { "President", "Olsen" };
	}
	@Override
	public void selectGame(String gameName) {
		if (gameName.equals("President")) {
			_ruleEngine = new RuleEngine();
		} else if(gameName.equals("Olsen")){
			_ruleEngine = new RuleEngineOlsen();
		}
	}

}