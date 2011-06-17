package sw805a.cardgame.game.models;

import java.util.Observable;

import flexjson.JSON;

public class Player extends Observable {
	private Hand _hand = new Hand();
	private String _address = "";
	private String _name = "";
	private int _id = -1;
	private boolean _myTurn = false;
	
	public Player() {}
	
	public Player(String address) {
		setAddress(address);
	}
	public Player(String address, Hand hand) {
		setAddress(address);
		setHand(hand);
	}
	public Player(String address, String name) {
		setAddress(address);
		setName(name);
	}
	public Player(String address, Hand hand, String name) {
		setAddress(address);
		setHand(hand);
		setName(name);
	}
	@JSON
	public Hand getHand() {
		return _hand;
	}
	public void setHand(Hand hand) {
		_hand = hand;
		setChanged();
		notifyObservers();
	}
	@JSON
	public String getAddress() {
		return _address;
	}
	public void setAddress(String address) {
		_address = address;
		setChanged();
		notifyObservers();
	}
	@JSON
	public String getName() {
		return (_name == null || _name.equals(""))?_address:_name;
	}
	public void setName(String name) {
		_name = name.trim();
		setChanged();
		notifyObservers();
	}
	
	@JSON
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}
	
	@JSON
	public boolean getMyTurn() {
		return _myTurn;
	}
	public void setMyTurn(boolean turn) {
		_myTurn = turn;
		setChanged();
		notifyObservers();
	}
	
}
