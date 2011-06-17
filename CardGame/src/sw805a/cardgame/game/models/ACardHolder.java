package sw805a.cardgame.game.models;

import java.util.ArrayList;
import java.util.Observable;

import flexjson.JSON;

public abstract class ACardHolder extends Observable {
	protected ArrayList<Card> _cards = new ArrayList<Card>();
	protected ArrayList<MoveHistory> _history = new ArrayList<MoveHistory>();
	
	public ACardHolder() {}
	
	@SuppressWarnings("unchecked")
	@JSON
	public ArrayList<Card> getCards() {
		return (ArrayList<Card>)_cards.clone(); // Ikke ligemeget!
	}
	public void setCards(ArrayList<Card> cards) {
		_cards = cards;
	}
	
	public void addCard(Player player, Card card) {
		_history.add(new MoveHistory(player, MoveHistory.MoveDirection.ADD, card));
		_cards.add(card);
		setChanged();
		notifyObservers(_cards);
	}
	
	public void addCards(Player player, ArrayList<Card> cards) {
		_history.add(new MoveHistory(player, MoveHistory.MoveDirection.ADD, cards));
		_cards.addAll(cards);
		setChanged();
		notifyObservers(_cards);
	}
	
	public boolean removeCard(Player player, Card card) {
		boolean result = _cards.remove(card);
		if (result) {
			_history.add(new MoveHistory(player, MoveHistory.MoveDirection.REMOVE, card));
			setChanged();
			notifyObservers(_cards);
		}
		return result;
	}
	
	public boolean removeCards(Player player, ArrayList<Card> cards) {
		boolean result = _cards.removeAll(cards);
		if (result) {
			_history.add(new MoveHistory(player, MoveHistory.MoveDirection.REMOVE, cards));
			setChanged();
			notifyObservers(_cards);
		}
		return result;
	}
	
	public void clearCards(Player player) {
		_history.add(new MoveHistory(player, MoveHistory.MoveDirection.REMOVE, _cards));
		_history.clear();
		_cards.clear();
		setChanged();
		notifyObservers(_cards);
	}
	
	@JSON(include=false)
	public int getCardCount() {
		return _cards.size();
	}
	
	@JSON(include=false)
	public Card getCard(int index) {
		return _cards.get(index);
	}

	@JSON(include=false)
	public ArrayList<Card> getSelectedCards() {
		ArrayList<Card> selectedCards = new ArrayList<Card>();
		for (Card card : getCards()) {
			if (card.isSelected()) {
				selectedCards.add(card);
			}
		}
		return selectedCards;
	}
	
	@JSON(include=false)
	public MoveHistory getLastMove() {
		if (_history.size() > 0) {
			return _history.get(_history.size() - 1);
		}
		return null;
	}
	
	@JSON(include=false)
	public ArrayList<MoveHistory> getMoveHistory() {
		return _history;
	}
}
