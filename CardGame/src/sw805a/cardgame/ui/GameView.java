package sw805a.cardgame.ui;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;

import sw805a.cardgame.game.GameEngine;
import sw805a.cardgame.game.IGameEngine;
import sw805a.cardgame.game.models.*;
import sw805a.cardgame.gesture.GestureEngine;
import sw805a.cardgame.gesture.OnGestureListener;
import sw805a.cardgame.ui.decorators.*;

public class GameView extends BaseGameActivity {
	private Camera _camera;
	private ViewEngine _viewEngine;
	private HashMap<Card, TextureRegion> _cardTotextureRegionMap = new HashMap<Card, TextureRegion>();
	private TextureRegion _backTextureRegion;
	private Font _blackFont,_whiteFont;

	public Engine onLoadEngine() {
		GestureEngine.getInstance().clearGestureListeners();
		_camera = new Camera(-15, 0, BoardDecorator.BOARD_WIDTH, BoardDecorator.BOARD_HEIGHT);
		final Engine engine = new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(BoardDecorator.BOARD_WIDTH, BoardDecorator.BOARD_HEIGHT), this._camera));		
		return engine;
	}

	public void onLoadResources() {
		Texture texture = new Texture(1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TextureRegionFactory.createFromAsset(texture, this, "gfx/carddeck2.png", 0, 0);
		
		_backTextureRegion = TextureRegionFactory.extractFromTexture(texture, 0, Card.Suit.SPECIAL.ordinal()*BoardDecorator.CARD_HEIGHT, BoardDecorator.CARD_WIDTH, BoardDecorator.CARD_HEIGHT);
		
		IGameEngine ge = GameEngine.getInstance();

		/* Extract the TextureRegion of each card in the whole deck. */
		for (Card card : ge.getRuleEngine().getGameState().getBoard().getDeckAsArray()) {
			addCard(texture, card);
		}
		
		mEngine.getTextureManager().loadTexture(texture);
		
		texture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		_blackFont = new Font(texture, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),20,true,Color.BLACK);
		_whiteFont = new Font(texture, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),20,true,Color.WHITE);
		
		mEngine.getTextureManager().loadTexture(texture);
		mEngine.getFontManager().loadFont(_blackFont);
		mEngine.getFontManager().loadFont(_whiteFont);
		
		_viewEngine = new ViewEngine(this, mEngine, _cardTotextureRegionMap, _backTextureRegion, _blackFont, _whiteFont);
	}
	
	private void addCard(Texture texture,Card card) {
		TextureRegion cardTextureRegion;
		if (!card.getSuit().equals(Card.Suit.SPECIAL)) {
			cardTextureRegion = TextureRegionFactory.extractFromTexture(texture, card.getValue().ordinal() * BoardDecorator.CARD_WIDTH, card.getSuit().ordinal() * BoardDecorator.CARD_HEIGHT, BoardDecorator.CARD_WIDTH, BoardDecorator.CARD_HEIGHT);
		} else {
			switch (card.getValue()) {
				case JOKER:
					cardTextureRegion = TextureRegionFactory.extractFromTexture(texture, BoardDecorator.CARD_WIDTH, card.getSuit().ordinal() * BoardDecorator.CARD_HEIGHT, BoardDecorator.CARD_WIDTH, BoardDecorator.CARD_HEIGHT);
					break;
				default:
					return;
			}
		}
		_cardTotextureRegionMap.put(card, cardTextureRegion);
	}

	public Scene onLoadScene() {
		Scene scene = new Scene(1);
		scene.setOnAreaTouchTraversalFrontToBack();
		_viewEngine.setScene(scene);
		
		IGameEngine ge = GameEngine.getInstance();
		Board board = ge.getRuleEngine().getGameState().getBoard();
		if (board != null) {
			ge.getRuleEngine().getBoardDecorator().decorate(_viewEngine, board);
		}
		scene.setTouchAreaBindingEnabled(true);

		
		return scene;
	}

	public void onLoadComplete() {
		GestureEngine.getInstance().addGestureListener(new OnGestureListener() {
			@Override
			public void onGesture(String id) {
				if(id == "flick"){
					GameEngine.getInstance().makeMove();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pEvent.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (pKeyCode) {
				case KeyEvent.KEYCODE_DPAD_CENTER:
					GameEngine.getInstance().makeMove();
					return true;
				case KeyEvent.KEYCODE_MENU:
					GameEngine.getInstance().makePass();
					return true;
				case KeyEvent.KEYCODE_BACK:
					GameEngine.getInstance().getCommunicator().disconnect();
					finish();
					return true;
			}
		}
		return super.onKeyDown(pKeyCode, pEvent); //similarily, this will allow actions other than key press to be processed elsewhere.
	}

}
