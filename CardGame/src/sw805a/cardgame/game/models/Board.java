package sw805a.cardgame.game.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import flexjson.JSON;

public class Board extends Observable {
	private ArrayList<Player> _players = new ArrayList<Player>();
	private ArrayList<Pile> _piles = new ArrayList<Pile>();
	private HashMap<Integer,Card> _deck = new HashMap<Integer,Card>();
	
	public Board() {
		
	}
	public Board(ArrayList<Player> players) {
		_players = players;
	}
	public Board(ArrayList<Player> players, ArrayList<Pile> piles) {
		_players = players;
		_piles = piles;
	}
	public HashMap<Integer,Card> getDeck() {
		return _deck;
	}
	public ArrayList<Card> getDeckAsArray() {
		ArrayList<Card> deck = new ArrayList<Card>();
		for (Map.Entry<Integer, Card> entry : getDeck().entrySet()) {
			deck.add(entry.getValue());
		}
		return deck;
	}
	public void setDeck(HashMap<Integer,Card> deck) {
		_deck = deck;
	}
	@JSON
	public ArrayList<Player> getPlayers() {
		return _players;
	}
	public void setPlayers(ArrayList<Player> players) {
		_players = players;
	}
	public int getPlayerCount() {
		return _players.size();
	}
	public Player getPlayer(int index) {
		return _players.get(index);
	}
	@JSON
	public ArrayList<Pile> getPiles() {
		return _piles;
	}
	public void setPiles(ArrayList<Pile> piles) {
		_piles = piles;
	}
	public int getPileCount() {
		return _piles.size();
	}
	public Pile getPile(int index) {
		return _piles.get(index);
	}
	
	
	public Player getPlayer(String address) {
		for (Player player : _players) {
			if (player.getAddress().equals(address)) {
				return player;
			}
		}
		return null;
	}
}
