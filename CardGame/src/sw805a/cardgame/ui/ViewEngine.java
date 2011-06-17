package sw805a.cardgame.ui;

import java.util.HashMap;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import sw805a.cardgame.game.models.Card;

public class ViewEngine {
	private GameView _gameView;
	private Engine _engine;
	private Scene _scene;
	private TextureRegion _backTextureRegion;
	private HashMap<Card, TextureRegion> _cardTotextureRegionMap = new HashMap<Card, TextureRegion>();
	private Font _blackFont,_whiteFont;
	
	public ViewEngine(GameView gameView, Engine engine, HashMap<Card, TextureRegion> cardTextureRegion, TextureRegion backTextureRegion, Font blackFont, Font whiteFont) {
		_gameView = gameView;
		_engine = engine;
		_cardTotextureRegionMap = cardTextureRegion;
		_backTextureRegion = backTextureRegion;
		_blackFont = blackFont;
		_whiteFont = whiteFont;
	}

	public GameView getGameView() {
		return _gameView;
	}
	
	public Engine getEngine() {
		return _engine;
	}
	
	public Scene getScene() {
		return _scene;
	}
	
	public void setScene(Scene scene) {
		_scene = scene;
	} 
	
	public HashMap<Card, TextureRegion> getCardTextureRegionMap() {
		return _cardTotextureRegionMap;
	}

	public TextureRegion getBackTextureRegion() {
		return _backTextureRegion;
	}
	
	public Font getBlackFont() {
		return _blackFont;
	}
	public Font getWhiteFont() {
		return _whiteFont;
	}
}
