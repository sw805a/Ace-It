package sw805a.cardgame.game.models;

import java.util.Observable;


public class Card extends Observable {
	
	// Do not reorder this, unless you reorder the texture image as well
	public enum Value
	{
		ACE,
		KING,
		QUEEN,
		JACK,
		TEN,
		NINE,
		EIGHT,
		SEVEN,
		SIX,
		FIVE,
		FOUR,
		THREE,
		TWO,
		JOKER
	}	
	// Same goes here
	public enum Suit {
		CLUB,
		SPADE,
		DIAMOND,
		HEART,
		SPECIAL
	}
	
	private Value _value;
	private Suit _suit;
	
	private boolean _selected = false;
	
	private int _sortingValue = 0;
	private int _pointValue;
	
	private int _id;
	
	public Suit getSuit() {
		return _suit;
	}
	public void setSuit(Suit suit) {
		_suit = suit;
	}
	public Value getValue() {
		return _value;
	}
	public void setValue(Value value) {
		_value = value;
	}
	
	public Card() {	}
	public Card(Suit suit,Value value) {
		_suit = suit;
		_value = value;
		_pointValue = value.ordinal();
	}
	
	public void setSelected(boolean selected) {
		_selected = selected;
		setChanged();
		notifyObservers();
	}
	
	public boolean isSelected() {
		return _selected;
	}
	
	public void setSortingValue(int value) {
		_sortingValue = value;
	}
	
	public int getSortingValue() {
		return _sortingValue;
	}
	
	public boolean equalValue(Card card) {
		return getValue().equals(card.getValue());
	}
	public boolean equalColor(Card card) {
		return getSuit().equals(card.getSuit());
	}
	public boolean equals(Card card) {
		return (equalColor(card) && equalValue(card));
	}
	public boolean equalPointValue(Card card) {
		return (getPointValue() == card.getPointValue());
	}
	
	public String toString() {
		return _suit+" "+_value;
	}

	public void setPointValue(int value) {
		_pointValue  = value;
	}
	public int getPointValue() {
		return _pointValue;
	}
	
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}
}
