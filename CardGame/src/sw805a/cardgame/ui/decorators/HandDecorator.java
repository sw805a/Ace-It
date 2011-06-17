package sw805a.cardgame.ui.decorators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


import sw805a.cardgame.game.models.Card;
import sw805a.cardgame.game.models.Hand;
import sw805a.cardgame.logger.Logger;
import sw805a.cardgame.ui.ViewEngine;
import sw805a.cardgame.ui.decorators.CardDecorator.Facing;
import sw805a.cardgame.ui.decorators.PlayerDecorator.Placement;

public class HandDecorator {
	public enum Sorting {
		BYVALUE,
		BYSUIT,
		BYSORTINGVALUE,
		BYSUITANDTHENVALUE,
		BYSUITANDTHENSORTINGVALUE,
		BYVALUEANDTHENSUIT,
		BYSORTINGVALUEANDTHENSUIT,
		BYPOINTVALUE,
		RANDOM
	}
	
	private ArrayList<Card> _cards = new ArrayList<Card>();
	private HashMap<Card, CardDecorator> _cardDecos = new HashMap<Card, CardDecorator>();
	
	private Facing _facing = Facing.UP;
	private Placement _placement;
	private Sorting _sorting = Sorting.RANDOM;
	
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
	
	public Sorting getSorting() {
		return _sorting;
	}
	
	public void setSorting(Sorting sorting) {
		_sorting = sorting;
	}
	
	public void decorate(final ViewEngine viewEngine, Hand hand) {
		_cards = hand.getCards();
		for (Card card : _cards) {
			CardDecorator cardDeco = new CardDecorator();
			cardDeco.setFacing(getFacing());
			switch(getPlacement()) {
				case TOP:
					cardDeco.setOrientation(CardDecorator.Orientation.SOUTH);
					break;
				case BOTTOM:
					cardDeco.setOrientation(CardDecorator.Orientation.NORTH);
					break;
				case LEFT:
					cardDeco.setOrientation(CardDecorator.Orientation.EAST);
					break;
				case RIGHT:
					cardDeco.setOrientation(CardDecorator.Orientation.WEST);
					break;
				default:
					return;
			}
			
			_cardDecos.put(card, cardDeco);
		}
		
		sortCards(_cards);
		
		doDecorate(viewEngine, _cards);
		
		
		// Lets listen for eventual updates
		hand.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				
				@SuppressWarnings("unchecked")
				ArrayList<Card> newCards = (ArrayList<Card>) data;
				
				ArrayList<Card> cardsToRemove = new ArrayList<Card>();
				for (Card card : _cards) {
					if (!newCards.contains(card)) {
						_cardDecos.get(card).remove();
						_cardDecos.remove(card);
						cardsToRemove.add(card); // DOH!!!!!
					}
				}
				// REMEMBER NOT TO REMOVE FROM THE THING YOUR ITERATING OVER FOOL
				for (Card card : cardsToRemove) {
					_cards.remove(card);
				}
					
				for (Card card : newCards) {
					if (!_cardDecos.containsKey(card)) {
						CardDecorator cardDeco = new CardDecorator();
						cardDeco.setFacing(getFacing());
						_cardDecos.put(card, cardDeco);
						_cards.add(card);
					}
				}
				
				sortCards(_cards);
				
				doDecorate(viewEngine, _cards);
			}
		});
	}
	
	public void doDecorate(final ViewEngine viewEngine,ArrayList<Card> cards) {
		int x,y, allocatedHandArea, numCards, displacement, rows = 1;
				
		switch(getPlacement()) {
			case TOP:
				allocatedHandArea = BoardDecorator.BOARD_WIDTH - 3 * BoardDecorator.CARD_VISIBLE_HEIGHT;
				break;
			case BOTTOM:
				allocatedHandArea = BoardDecorator.BOARD_WIDTH - 3 * BoardDecorator.CARD_VISIBLE_HEIGHT;
				break;
			case LEFT:
				allocatedHandArea = BoardDecorator.BOARD_HEIGHT - BoardDecorator.CARD_VISIBLE_HEIGHT;
				break;
			case RIGHT:
				allocatedHandArea = BoardDecorator.BOARD_HEIGHT - BoardDecorator.CARD_VISIBLE_HEIGHT;
				break;
			default:
				return;
		}
		numCards = cards.size();
		
		switch(getPlacement()) {
			case TOP:
				y = BoardDecorator.CARD_VISIBLE_HEIGHT - BoardDecorator.CARD_HEIGHT;
				if (numCards * BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area in full size
					x = BoardDecorator.BOARD_WIDTH / 2 + numCards * BoardDecorator.CARD_WIDTH / 2;
					displacement = BoardDecorator.CARD_WIDTH;
				}else if ((numCards-1) * BoardDecorator.CARD_MIN_DISTANCE + BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area covering each other
					x = BoardDecorator.BOARD_WIDTH / 2 + allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / (numCards - 1);
				} else {
					// 2 rows is needed
					rows = 2;
					x = BoardDecorator.BOARD_WIDTH / 2 + allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.ceil((double)numCards/2) - 1);
				}
				x = x - BoardDecorator.CARD_WIDTH;
				break;	
			case BOTTOM:			
				y = BoardDecorator.BOARD_HEIGHT - BoardDecorator.CARD_VISIBLE_HEIGHT;
				if (numCards * BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area in full size
					x = BoardDecorator.BOARD_WIDTH / 2 - numCards * BoardDecorator.CARD_WIDTH / 2;
					displacement = BoardDecorator.CARD_WIDTH;
				}else if ((numCards-1) * BoardDecorator.CARD_MIN_DISTANCE + BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area covering each other
					x = BoardDecorator.BOARD_WIDTH / 2 - allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / (numCards - 1);
				} else {
					// 2 rows is needed
					rows = 2;
					x = BoardDecorator.BOARD_WIDTH / 2 - allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.ceil((double)numCards/2) - 1);
				}
				break;

			case LEFT:
				x = BoardDecorator.CARD_VISIBLE_HEIGHT - BoardDecorator.CARD_HEIGHT;
				if (numCards * BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area in full size
					y = BoardDecorator.BOARD_HEIGHT / 2 - numCards * BoardDecorator.CARD_WIDTH / 2;
					displacement = BoardDecorator.CARD_WIDTH;
				}else if ((numCards-1) * BoardDecorator.CARD_MIN_DISTANCE + BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area covering each other
					y = BoardDecorator.BOARD_HEIGHT / 2 - allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / (numCards - 1);
				} else {
					// 2 rows is needed
					rows = 2;
					y = BoardDecorator.BOARD_HEIGHT / 2 - allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.ceil((double)numCards/2) - 1);
				}
				break;
			case RIGHT:
				x = BoardDecorator.BOARD_WIDTH - BoardDecorator.CARD_VISIBLE_HEIGHT; 
				if (numCards * BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area in full size
					y = BoardDecorator.BOARD_HEIGHT / 2 + numCards * BoardDecorator.CARD_WIDTH / 2;
					displacement = BoardDecorator.CARD_WIDTH;
				}else if ((numCards-1) * BoardDecorator.CARD_MIN_DISTANCE + BoardDecorator.CARD_WIDTH <= allocatedHandArea) {
					// If all cards can be in area covering each other
					y = BoardDecorator.BOARD_HEIGHT / 2 + allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / (numCards - 1);
				} else {
					// 2 rows is needed
					rows = 2;
					y = BoardDecorator.BOARD_HEIGHT / 2 + allocatedHandArea / 2;
					displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.ceil((double)numCards/2) - 1);
				}
				y = y - BoardDecorator.CARD_WIDTH;
				break;
			default:
				Logger.Log("DECORATOR", "Player placement not yet implemented: " + getPlacement());
				return;
		}
			

		// Decorate each card.. plz
		for (int i = 0, j = 0; i < numCards; i++, j++) {
			if (rows == 2 && j == (int)Math.ceil((double)numCards / 2)) {
				j = 0;
				switch (getPlacement()) {
					case TOP:
						displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.floor((double)numCards/2) - 1);
						y = y - BoardDecorator.CARD_VISIBLE_HEIGHT / 2;
						break;
					case BOTTOM:
						displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.floor((double)numCards/2) - 1);
						y = y + BoardDecorator.CARD_VISIBLE_HEIGHT / 2;
						break;
					case LEFT:
						displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.floor((double)numCards/2) - 1);
						x = x - BoardDecorator.CARD_VISIBLE_HEIGHT / 2;
						break;
					case RIGHT:
						displacement = (allocatedHandArea - BoardDecorator.CARD_WIDTH) / ((int)Math.floor((double)numCards/2) - 1);
						x = x + BoardDecorator.CARD_VISIBLE_HEIGHT / 2;
						break;
					default:
						break;
				}
			}
			int cardX = x;
			int cardY = y;
			
			switch (getPlacement()) {
				case TOP:
					cardX = x - displacement * j;
					break;
				case BOTTOM:
					cardX = x + displacement * j;
					break;
				case LEFT:
					cardY = y + displacement * j;
					break;
				case RIGHT:
					cardY = y - displacement * j;
					break;
				default:
					return;
			}

			_cardDecos.get(cards.get(i)).decorate(viewEngine, cards.get(i), cardX, cardY, i);
		}
	}
	
	private void sortCards(ArrayList<Card> _cards) {
		switch(getSorting()) {
			case RANDOM:
				Collections.shuffle(_cards);
				break;
			case BYVALUE:
				Collections.sort(_cards, new Comparator<Card>() {
					@Override
					public int compare(Card card1, Card card2) {
						return (card2.getValue().ordinal() - card1.getValue().ordinal());
					}
				});
				break;
			case BYSORTINGVALUE:
				Collections.sort(_cards, new Comparator<Card>() {
					@Override
					public int compare(Card card1, Card card2) {
						return (card2.getSortingValue() - card1.getSortingValue());
					}
				});
				break;
			case BYPOINTVALUE:
				Collections.sort(_cards, new Comparator<Card>() {
					@Override
					public int compare(Card card1, Card card2) {
						return (card1.getPointValue() - card2.getPointValue());
					}
				});
				break;
			default:
				Logger.Log("DECORATOR","Sorting not implemented: " + getSorting());
				break;
		}
	}
}
