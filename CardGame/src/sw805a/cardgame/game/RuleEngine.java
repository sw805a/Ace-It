package sw805a.cardgame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import sw805a.cardgame.game.models.Board;
import sw805a.cardgame.game.models.Card;
import sw805a.cardgame.game.models.Card.Suit;
import sw805a.cardgame.game.models.Card.Value;
import sw805a.cardgame.game.models.GameState;
import sw805a.cardgame.game.models.Hand;
import sw805a.cardgame.game.models.Move;
import sw805a.cardgame.game.models.MoveHistory;
import sw805a.cardgame.game.models.Pile;
import sw805a.cardgame.game.models.Player;
import sw805a.cardgame.logger.Logger;
import sw805a.cardgame.ui.decorators.BoardDecorator;
import sw805a.cardgame.ui.decorators.CardDecorator.Facing;
import sw805a.cardgame.ui.decorators.PileDecorator;
import sw805a.cardgame.ui.decorators.PlayerDecorator;
import sw805a.cardgame.ui.decorators.HandDecorator.Sorting;

public class RuleEngine implements IRuleEngine {
	
	private GameState _gameState = new GameState();
	private Player _turn;
	private Player _myPlayer;
	
	private BoardDecorator _boardDeco = null;
	
	@Override
	public GameState getGameState() {
		return _gameState;
	}		

	
	/**
	 * Parts for initializing the game
	 * 
	 */
	
	private void initializePiles() {
		// TODO: Load from XML
		Pile p = new Pile();
		ArrayList<Pile> piles = new ArrayList<Pile>();
		piles.add(p);
		getGameState().getBoard().setPiles(piles);
	}
	private void initializeDeck() {
		//  TODO: Load from XML
		HashMap<Integer,Card> deck = new HashMap<Integer,Card>();
		// Initialize deck
		int id = 0;
		for (Card.Suit col : Card.Suit.values()) {
			if (col.equals(Card.Suit.SPECIAL)) continue;
			for (Card.Value val : Card.Value.values()) {
				if (val.equals(Card.Value.JOKER)) continue;
				Card card = new Card(col,val);
				card.setId(id);
				card.setPointValue(calcCardPointValue(card));
				deck.put(id++,card);
			}
		}
		for (int i = id; i<id+3;i++) {
			Card joker = new Card(Card.Suit.SPECIAL, Card.Value.JOKER);
			joker.setId(i);
			deck.put(i, joker);
		}
		
		getGameState().getBoard().setDeck(deck);
	}
	public void initializeGame(Player myPlayer, ArrayList<Player> players) {
		_myPlayer = myPlayer;
		getGameState().getBoard().setPlayers(players);
		
		initializeDeck();
		initializePiles();
		
		
		// TODO: Load from xml
		/**
		 * R�vhul specific
		 */
		Board board = getGameState().getBoard();
		int playerCount = board.getPlayerCount();
		
		ArrayList<Card> deck = board.getDeckAsArray();
		Collections.shuffle(deck);
		
		int i = 0;
		for (Card card : deck) {
			board.getPlayer(i++ % playerCount).getHand().addCard(null, card);
		}
		
		calcStarter();
	}
	private void initializeBoardDecorator() {
		// TODO: Load from xml
		BoardDecorator boardDeco = new BoardDecorator();
		PileDecorator pileDeco = new PileDecorator();
		boardDeco.addPileDecorator(pileDeco);
		
		PlayerDecorator player1Deco = new PlayerDecorator();
		player1Deco.setPlacement(PlayerDecorator.Placement.BOTTOM);
		player1Deco.setSorting(Sorting.BYPOINTVALUE);
		
		PlayerDecorator player2Deco = new PlayerDecorator();
		player2Deco.setPlacement(PlayerDecorator.Placement.TOP);
		player2Deco.setFacing(Facing.DOWN);
		
		PlayerDecorator player3Deco = new PlayerDecorator();
		player3Deco.setPlacement(PlayerDecorator.Placement.LEFT);
		player3Deco.setFacing(Facing.DOWN);
		
		PlayerDecorator player4Deco = new PlayerDecorator();
		player4Deco.setPlacement(PlayerDecorator.Placement.RIGHT);
		player4Deco.setFacing(Facing.DOWN);
		
		boardDeco.addPlayerDecorator(player1Deco);
		boardDeco.addPlayerDecorator(player2Deco);
		boardDeco.addPlayerDecorator(player3Deco);
		boardDeco.addPlayerDecorator(player4Deco);
		
		_boardDeco = boardDeco;
	}
	@Override
	public BoardDecorator getBoardDecorator() {	
		if (_boardDeco == null) initializeBoardDecorator();
		return _boardDeco;
	}
	
	private int moveCount = 0;
	/**
	 *  Parts for actual r�vhul rules
	 */
	private boolean isCardSelectable(Card card) {
		// TODO: Load these rules from XML
		
		// If its not ones turn
		if (!_turn.equals(_myPlayer))
			return false;

		Hand myHand = _myPlayer.getHand();
		
		MoveHistory lastMove = getGameState().getBoard().getPile(0).getLastMove();
		// Check if first move and check if three of clubs is selected
		if (moveCount == 0) {
			if (myHand.getSelectedCards().size() > 0) {
				boolean found = false;
				for (Card aCard : myHand.getSelectedCards()) {
					if (aCard.getSuit() == Suit.CLUB && aCard.getValue() == Value.THREE) {
						found = true;
						break;
					}
				}
				if (!found && !(card.getSuit() == Suit.CLUB && card.getValue() == Value.THREE)) {
					return false;
				}
				if ((card.getSuit() == Suit.CLUB && card.getValue() == Value.THREE) && card.isSelected()) {
					return false;
				}
			} else {
				if (!(card.getSuit() == Suit.CLUB && card.getValue() == Value.THREE)) {
					return false;
				}
			}
		}
		
		// IF TABLE IS NOT EMPTY
		if (lastMove != null && lastMove.getCardCount() > 0) {
			int movePointValue = lastMove.getCards().get(0).getPointValue();
			// If your card is less than table IGNORE
			if (card.getPointValue() < movePointValue) {
				return false;
			}
			
			
			// Check if you already has selected other card
			if (myHand.getSelectedCards().size() > 0) {
				if (!myHand.getSelectedCards().get(0).equalPointValue(card))
					return false;
			}
			
			
			// Check if you have the right amount and not a rydder
			if (card.getPointValue() != 13) {
				
				int count = 0;
				for (Card aCard : myHand.getCards()) {
					if (aCard.equalValue(card)) {
						count++;
					}
				}
				if (count < lastMove.getCardCount())
					return false;
				
				if (myHand.getSelectedCards().size() == lastMove.getCardCount() && !card.isSelected())
					return false;
			}			
		} else {
			// If table is empty
			// Check if you already has selected other card
			if (myHand.getSelectedCards().size() > 0) {
				if (!myHand.getSelectedCards().get(0).equalPointValue(card))
					return false;
			}
		}
		return true;
	}
	@Override
	public void selectCard(Card card) {
		if (isCardSelectable(card)) {
			card.setSelected(!card.isSelected());
		}
	}

	private boolean isMoveAllowed(Player player, ArrayList<Card> cards) {
		if (!_turn.equals(player))
			return false;
		
		MoveHistory lastMove = getGameState().getBoard().getPile(0).getLastMove();
		
		// If you have no selected cards
		if (cards.size() == 0)
			 return false;
		
		// Check if the same amount of cards is played
		if (lastMove != null && cards.get(0).getPointValue() != 13 && lastMove.getCardCount() > 0 && cards.size() != lastMove.getCardCount())
			return false;
		
		return true;
	}
	private boolean makeMove(Player player, ArrayList<Card> selectedCards) {
		if (!isMoveAllowed(player, selectedCards)) {
			return false;
		}
		
		
		// Because the communication returns other than actual card, we need to find the phone representation of it
		ArrayList<Card> phoneMemoryCard = new ArrayList<Card>();
		
		for (Card card : selectedCards) {
			phoneMemoryCard.add(getGameState().getBoard().getDeck().get(card.getId()));
		}
		
		player.getHand().removeCards(player, phoneMemoryCard);
		Pile pile = getGameState().getBoard().getPile(0);
		pile.addCards(player, phoneMemoryCard);
		
		
		for (Card card : selectedCards) {
			card.setSelected(false);
		}
		
		moveCount++;
		
		//Thread.sleep(5000);
		// Check if the player cleans the table 
		if (selectedCards.get(0).getPointValue() == 13) {
			pile.clearCards(player);
			return true;
		}
		
		if (pile.getCardCount() >= 4) {
			Card topCard = pile.getTopCard();
			boolean pileTopFourEqual = true;
			for (int i = 1; i < 4; i++) {
				Card topCardOffset = pile.getTopCard(i); 
				if (!topCard.equalPointValue(topCardOffset)) {
					pileTopFourEqual = false;
					break;
				}
			}
			if (pileTopFourEqual) {
				pile.clearCards(player);
				return true;
			}
		}
		
		
		nextTurn(player);
		return true;
	}
	private boolean makePass(Player player) {
		if (!_turn.equals(player))
			return false;
		
		if (moveCount != 0) {
			nextTurn(player);
			for (Card card : player.getHand().getSelectedCards()) {
				card.setSelected(false);
			}
			
			// Clear pile if passed all the way around
			Pile pile = getGameState().getBoard().getPile(0);
			int id = (player.getId() + 1) % getGameState().getBoard().getPlayerCount();
			Player nextPlayer = null;
			for (Player p : getGameState().getBoard().getPlayers()) {
				if (p.getId() == id) {
					nextPlayer = p;
					break;
				}
			}
			if (nextPlayer == null)
				return false;
			
			
			if (pile.getLastMove() != null && pile.getLastMove().getPlayer().equals(nextPlayer)) {
				pile.clearCards(nextPlayer);
			}
			return true;
		}
		return false;
	}

	private void nextTurn(Player currentPlayer) {
		ArrayList<Player> players = getGameState().getBoard().getPlayers();
		for (int i=0;i<players.size(); i++) {
			if (players.get(i).equals(currentPlayer)) {
				for (int j = 1; j < players.size(); j++) {
					if (players.get((i+j)%players.size()).getHand().getCardCount() > 0) {
						_turn.setMyTurn(false);
						_turn = getGameState().getBoard().getPlayer((i+j)%players.size());
						_turn.setMyTurn(true);
						Logger.Log("RULE","New turn: "+_turn.getName());
						break;
					}
				}
				break;
			}
		}
	}
	
 	private int calcCardPointValue(Card card) {
		// TODO: Load this from XML
		switch (card.getValue()) {
			case THREE:
				return 1;
			case FOUR:
				return 2;
			case FIVE:
				return 3;
			case SIX:
				return 4;
			case SEVEN:
				return 5;
			case EIGHT:
				return 6;
			case NINE:
				return 7;
			case JACK:
				return 8;
			case QUEEN:
				return 9;
			case KING:
				return 10;
			case ACE:
				return 11;
			case TWO:
				return 12;
			case TEN:
			case JOKER:
				return 13;
			default:
				return 0;
		}
	}
	private void calcStarter() {
		moveCount = 0;
		for (Player player : getGameState().getBoard().getPlayers()) {
			for (Card card : player.getHand().getCards()) {
				if (card.getSuit() == Suit.CLUB && card.getValue() == Value.THREE) {
					_turn = player;
					_turn.setMyTurn(true);
					return;
				}
			}
		}
	}
	@Override
	public void receiveGameState(Player myPlayer, GameState gameState) {
		_gameState = gameState;
		_myPlayer = myPlayer;
		calcStarter();
	}
	@Override
	public void recieveMove(Move move) {
		for (Player player : getGameState().getBoard().getPlayers()) {
			if (player.getAddress().equals(move.getPlayer().getAddress())) {
				move.setPlayer(player);
				break;
			}
		}
		
		
		if (move.getCards().size() > 0) {
			makeMove(move.getPlayer(),move.getCards());
		} else {
			makePass(move.getPlayer());
		}
	}
	@Override
	public Move makeMove() {
		Move move = new Move(_myPlayer, _myPlayer.getHand().getSelectedCards());
		if (move.getCards().size() > 0) {
			if (makeMove(move.getPlayer(),move.getCards())) {
				return move;
			}
		} else {
			if (makePass(move.getPlayer())) {
				return move;
			}
		}
		return null;
	}
	
}
