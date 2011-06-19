package sw805a.cardgame.game;

import java.util.ArrayList;

import sw805a.cardgame.comm.Client;
import sw805a.cardgame.comm.CommunicationType;
import sw805a.cardgame.comm.IGameCommunication;
import sw805a.cardgame.game.models.Card;

public interface IGameEngine {
	IGameCommunication getCommunicator();

	/**
	 * 
	 * @param type
	 */
	void setCommunicationType(CommunicationType type);
	
	
	/**
	 * 
	 */
	String[] getAvailableGames();
	void selectGame(String gameName);
	
	/**
	 * invites the players in the players array
	 */
	void inviteClients();
	Client getDisconnectedClient();	
	void setDisconnectedClient(Client client);
	void answerInvite(boolean answer);
	// Get and set current players
	ArrayList<Client> getClients();
	void setClients(ArrayList<Client> clients);
	
	boolean isHost();
	void hardReset();
	
	
	RuleEngine getRuleEngine();
	void selectCard(Card card);
	void makeMove();

	void makePass();
}
