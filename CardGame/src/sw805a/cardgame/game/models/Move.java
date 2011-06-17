package sw805a.cardgame.game.models;

import java.util.ArrayList;

import flexjson.JSON;

public class Move {
	private ArrayList<Card> _cards = new ArrayList<Card>();
	private Player _player;
	public Move() {}
	
	public Move(Player player, ArrayList<Card> cards) {
		_player = player;
		_cards = cards;
	}
	@JSON
	public ArrayList<Card> getCards() {
		return _cards;
	}
	public void setCards(ArrayList<Card> cards) {
		_cards = cards;
	}
	@JSON
	public Player getPlayer() {
		return _player;
	}
	public void setPlayer(Player player) {
		_player = player;
	}
}
