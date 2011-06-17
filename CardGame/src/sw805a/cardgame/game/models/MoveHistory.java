package sw805a.cardgame.game.models;

import java.util.ArrayList;

public class MoveHistory {
	public static enum MoveDirection {
		ADD,
		REMOVE
	};
	private MoveDirection _direction;
	private ArrayList<Card> _cards = new ArrayList<Card>();
	private Player _player;
	
	public MoveHistory() {}
	public MoveHistory(Player player, MoveDirection direction, ArrayList<Card> cards) {
		_player = player;
		_direction = direction;
		_cards = cards;
	}
	public MoveHistory(Player player, MoveDirection direction, Card card) {
		_player = player;
		_direction = direction;
		_cards.add(card);
	}
	
	public Player getPlayer() {
		return _player;
	}
	public MoveDirection getDirection() {
		return _direction;
	}
	public ArrayList<Card> getCards() {
		return _cards;
	}
	public int getCardCount() {
		return _cards.size();
	}

}
