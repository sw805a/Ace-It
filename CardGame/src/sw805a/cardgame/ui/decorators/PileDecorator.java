package sw805a.cardgame.ui.decorators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import sw805a.cardgame.game.models.Card;
import sw805a.cardgame.game.models.Pile;
import sw805a.cardgame.logger.Logger;
import sw805a.cardgame.ui.ViewEngine;
import sw805a.cardgame.ui.decorators.CardDecorator.Facing;

public class PileDecorator {
	public enum Placement {
		TOP_LEFT,
		TOP_LEFTCENTER,
		TOP_RIGHTCENTER,
		TOP_RIGHT,
		CENTER,
		BOTTOM_LEFT,
		BOTTOM_LEFTCENTER,
		BOTTOM_RIGHTCENTER,
		BOTTOM_RIGHT
	};
	public enum VisibleCards {
		FIXED_NUMBER,
		LASTADDMOVE,
		ALL
	}
	
	private Facing _facing = Facing.UP;
	private Placement _placement = Placement.CENTER;
	private VisibleCards _visibleCards = VisibleCards.LASTADDMOVE;
	private int _visibleCardsCount = 1;
	private HashMap<Card,CardDecorator> _cardDecos = new HashMap<Card, CardDecorator>();
	private ArrayList<Card> _cards = new ArrayList<Card>();

	public Facing getFacing() {
		return _facing;
	}
	public void setFacing(Facing facing) {
		_facing = facing;
	}
	
	public Placement getPlacement() {
		return _placement;
	}
	public void setPlacement(Placement placement) {
		_placement = placement;
	}

	public VisibleCards getVisibleCards() {
		return _visibleCards;
	}
	public void setVisibleCards(VisibleCards visibleCards) {
		_visibleCards = visibleCards;
	}

	public int getVisibleCardsCount() {
		return _visibleCardsCount;
	}
	public void setVisibleCardsCount(int visibleCardsCount) {
		_visibleCardsCount = visibleCardsCount;
	}
	
	public void decorate(final ViewEngine viewEngine, final Pile pile) {
		switch (getVisibleCards()) {
			// TODO: Clone this shit to avoid updating from outside
			case ALL:
				_cards = pile.getCards();
				break;
			case LASTADDMOVE:
				_cards.clear();
				// TODO: This is not right, as the last add move cards might have been removed
				if (pile.getLastAddMove() != null)
					_cards.addAll(pile.getLastAddMove().getCards());
				break;
			case FIXED_NUMBER:
				_cards.clear();
				for (int i = 0; i < getVisibleCardsCount(); i++) {
					_cards.add(pile.getTopCard(i));
				}
				break;
		}
		
		for (Card card : _cards) {
			CardDecorator cardDeco = new CardDecorator();
			cardDeco.setFacing(getFacing());	
			_cardDecos.put(card, cardDeco);
		}
		doDecorate(viewEngine,_cards);
		
		pile.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {						
				ArrayList<Card> cardsToRemove = new ArrayList<Card>();

				for (Card card : _cards) {
					_cardDecos.get(card).remove();
					_cardDecos.remove(card);
					cardsToRemove.add(card); // DOH!!!!!
				}
				// REMEMBER NOT TO REMOVE FROM THE THING YOUR ITERATING OVER FOOL
				for (Card card : cardsToRemove) {
					_cards.remove(card);
				}
				
				switch (getVisibleCards()) {
					case ALL:
						// TODO: Clone this shit to avoid updating from outside
						_cards = pile.getCards();
						break;
					case LASTADDMOVE:
						_cards.clear();
						// TODO: This is not right, as the last add move cards might have been removed
						if (pile.getLastMove() != null)
							_cards.addAll(pile.getLastMove().getCards());
						break;
					case FIXED_NUMBER:
						_cards.clear();
						for (int i = 0; i < getVisibleCardsCount(); i++) {
							_cards.add(pile.getTopCard(i));
						}
						break;
				}
				for (Card card : _cards) {
					CardDecorator cardDeco = new CardDecorator();
					cardDeco.setFacing(getFacing());	
					_cardDecos.put(card, cardDeco);
				}

				doDecorate(viewEngine, _cards);
			}
		});
		
	}
	public void doDecorate(ViewEngine viewEngine, ArrayList<Card> cards) {
		int x, y, numCards;
		
		// Find center of pile
		switch (getPlacement()) {
			case CENTER:
				x = BoardDecorator.BOARD_WIDTH / 2;
				y = BoardDecorator.BOARD_HEIGHT / 2;
				break;
	
			default:
				Logger.Log("DECORATOR", "Pile placement not yet implemented: " + getPlacement());
				return;
		}
		
		// Calculate card positions
		switch (getVisibleCards()) {
			case LASTADDMOVE:
				numCards = cards.size();
				break;
			default:
				Logger.Log("DECORATOR", "Visible card placement not yet implemented: " + getVisibleCards());
				return;
		}
		
		// Draw pile top cards
		x = x - numCards * BoardDecorator.CARD_WIDTH / 2;
		y = y - BoardDecorator.CARD_HEIGHT / 2;
		
		for (int i = 0; i < numCards; i++) {
			Card card = cards.get(i);
			_cardDecos.get(card).decorate(viewEngine, card, x + BoardDecorator.CARD_WIDTH * i, y, i);
		}
	}
}
