package sw805a.cardgame.ui.decorators;

import java.util.Observable;
import java.util.Observer;

import org.anddev.andengine.opengl.buffer.BufferObjectManager;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import sw805a.cardgame.game.models.Card;
import sw805a.cardgame.ui.ViewEngine;
import sw805a.cardgame.ui.decorators.helpers.CardSprite;

public class CardDecorator {
	public enum Facing {
		UP,
		DOWN
	}
	public enum Orientation {
		NORTH,
		SOUTH,
		EAST,
		WEST
	}
	private Orientation _orientation = Orientation.NORTH;
	private Facing _facing = Facing.UP;
	private CardSprite _sprite;
	
	public Orientation getOrientation() {
		return _orientation;
	}
	public void setOrientation(Orientation orientation) {
		_orientation = orientation;
	}
	public void setFacing(Facing facing) {
		_facing = facing;
	}
	public Facing getFacing() {
		return _facing;
	}

	private Card _card;
	
	private int _startX;
	private int _startY;
	
	private Observer _observer;
	
	private ViewEngine _viewEngine;
		
	public void decorate(final ViewEngine viewEngine, final Card card, int x, int y, int zIndex) {
		int rotation = 0;
		
		_viewEngine = viewEngine;
		_card = card;
		_startX = x;
		_startY = y;
		
		switch(getOrientation()) {
			case NORTH:
				if (card.isSelected())
					y = y - BoardDecorator.CARD_SELECTED_OFFSET;
				break;
			case SOUTH:
				if (card.isSelected())
					y = y + BoardDecorator.CARD_SELECTED_OFFSET;				
				rotation = 180;
				break;
			case EAST:
				if (card.isSelected())
					x = x - BoardDecorator.CARD_SELECTED_OFFSET;
				rotation = 90;
				break;
			case WEST:
				if (card.isSelected())
					x = x + BoardDecorator.CARD_SELECTED_OFFSET;
				rotation = 270;
				break;
		}
		
		
		if (_sprite != null) {
           	viewEngine.getScene().unregisterTouchArea(_sprite);
           	_sprite.setPosition(x, y);		
           	_sprite.setZIndex(zIndex);
           	viewEngine.getScene().getLastChild().sortChildren();
           	viewEngine.getScene().registerTouchArea(_sprite);

		} else {
			if (getFacing().equals(Facing.UP)) {
				TextureRegion tr = viewEngine.getCardTextureRegionMap().get(card);
				_sprite = new CardSprite(card, x, y, tr);
			} else {
				_sprite = new CardSprite(card, x, y, viewEngine.getBackTextureRegion());
			}
			_sprite.setRotation(rotation);
			viewEngine.getScene().getLastChild().attachChild(_sprite);
			_sprite.setZIndex(zIndex);
			viewEngine.getScene().getLastChild().sortChildren();
			
			viewEngine.getScene().registerTouchArea(_sprite);
			
			_observer = new Observer() {
				@Override
				public void update(Observable observable, Object data) {
					if (card.isSelected()) {
						switch(_orientation) {
							case NORTH:
								_sprite.setPosition(_startX, _startY - BoardDecorator.CARD_SELECTED_OFFSET);
								break;
							case SOUTH:
								_sprite.setPosition(_startX, _startY + BoardDecorator.CARD_SELECTED_OFFSET);
								break;
							case EAST:
								_sprite.setPosition(_startX + BoardDecorator.CARD_SELECTED_OFFSET,_startY);
								break;
							case WEST:
								_sprite.setPosition(_startX - BoardDecorator.CARD_SELECTED_OFFSET,_startY);
								break;
						}
					} else {
						_sprite.setPosition(_startX, _startY);
					}
				}
			};
			card.addObserver(_observer);
		}
		
		//viewEngine.getScene().registerTouchArea(sprite);
	}
	public void remove() {

		_card.deleteObserver(_observer);
		_viewEngine.getGameView().runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	_viewEngine.getScene().unregisterTouchArea(_sprite);
            	_viewEngine.getScene().detachChild(_sprite);
            	BufferObjectManager.getActiveInstance().unloadBufferObject(_sprite.getVertexBuffer());
            }
		});

	}
}
