package sw805a.cardgame.ui.decorators;

import java.util.Observable;
import java.util.Observer;

import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.util.HorizontalAlign;

import sw805a.cardgame.game.models.Player;
import sw805a.cardgame.ui.ViewEngine;
import sw805a.cardgame.ui.decorators.CardDecorator.Facing;
import sw805a.cardgame.ui.decorators.HandDecorator.Sorting;

public class PlayerDecorator {
	
	public enum Placement {
		TOP,
		LEFT,
		RIGHT,
		BOTTOM
	}
	
	private HandDecorator _handDeco = new HandDecorator();
	private Placement _placement;
	private Text _playername = null;
	
	
	public Placement getPlacement() {
		return _placement;
	}

	public void setPlacement(Placement placement) {
		_placement = placement;
		_handDeco.setPlacement(placement);
	}
	
	public Facing getFacing() {
		return _handDeco.getFacing();
	}
	public void setFacing(Facing facing) {
		_handDeco.setFacing(facing);
	}
	
	public Sorting getSorting() {
		return _handDeco.getSorting();
	}
	public void setSorting(Sorting sorting) {
		_handDeco.setSorting(sorting);
	}
	
	public void decorate(final ViewEngine viewEngine, final Player player) {
		player.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				if (player.getMyTurn()) {
					_playername.setColor(1f, 1f, 1f);
				} else {
					_playername.setColor(0f, 0f, 0f);
				}
			}
		});
		
		int x, y;

		// Draw the player names
		switch (getPlacement()) {
			case TOP:
				x = BoardDecorator.BOARD_WIDTH / 2;
				y = BoardDecorator.CARD_VISIBLE_HEIGHT + BoardDecorator.PLAYERNAME_TEXT_DISTANCE;
				break;
			case BOTTOM:
				x = BoardDecorator.BOARD_WIDTH / 2;
				y = BoardDecorator.BOARD_HEIGHT - BoardDecorator.CARD_VISIBLE_HEIGHT - BoardDecorator.PLAYERNAME_TEXT_DISTANCE - viewEngine.getBlackFont().getLineHeight();				
				break;
			case LEFT:
				y = BoardDecorator.BOARD_HEIGHT / 2;
				x = BoardDecorator.CARD_VISIBLE_HEIGHT + BoardDecorator.PLAYERNAME_TEXT_DISTANCE; 
				break;
			case RIGHT:
				y = BoardDecorator.BOARD_HEIGHT / 2;
				x = BoardDecorator.BOARD_WIDTH - BoardDecorator.CARD_VISIBLE_HEIGHT - BoardDecorator.PLAYERNAME_TEXT_DISTANCE - viewEngine.getBlackFont().getLineHeight();
				break;
			default:
					return;
					
		}
		
		if (_playername == null) {
			_playername = new Text(x, y, viewEngine.getWhiteFont(), player.getName(), HorizontalAlign.CENTER);
			viewEngine.getScene().getLastChild().attachChild(_playername);
		}
		if (player.getMyTurn()) {
			_playername.setColor(1f, 1f, 1f);
		} else {
			_playername.setColor(0f, 0f, 0f);
		}
		
		switch (getPlacement()) {
			case BOTTOM:
			case TOP:
				_playername.setPosition(x - _playername.getWidth() /2 , y);
				break;
			case LEFT:
				_playername.setRotation(90);
				_playername.setPosition(x - _playername.getWidth() / 2, y - _playername.getHeight() / 2);
				break;
			case RIGHT:
				_playername.setRotation(270);
				_playername.setPosition(x - _playername.getWidth() / 2, y - _playername.getHeight() / 2);
				break;
		}

		
		// Draw all the cards
		_handDeco.decorate(viewEngine, player.getHand());
	}
}
