package sw805a.cardgame.ui.decorators.helpers;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import sw805a.cardgame.game.GameEngine;
import sw805a.cardgame.game.models.Card;

public class CardSprite extends Sprite {
	private final Card _card;
	public CardSprite(Card card, float pX, float pY, TextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion);
		_card = card;
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		switch(pSceneTouchEvent.getAction()) {
			case TouchEvent.ACTION_DOWN:
				GameEngine.getInstance().selectCard(_card);
				break;
			case TouchEvent.ACTION_MOVE:
				break;
			case TouchEvent.ACTION_UP:
				break;
		}
		return true;
	}


}
