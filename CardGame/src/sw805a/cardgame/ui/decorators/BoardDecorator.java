package sw805a.cardgame.ui.decorators;

import java.util.ArrayList;

import org.anddev.andengine.entity.scene.background.ColorBackground;

import sw805a.cardgame.game.models.Board;
import sw805a.cardgame.logger.Logger;
import sw805a.cardgame.ui.ViewEngine;

public class BoardDecorator {
	
	public enum BackgroundStyle {
		COLOR,
		TEXTURE
	}
	public static int BOARD_WIDTH = 720;
	public static int BOARD_HEIGHT = 480;
	public static int CARD_MIN_DISTANCE = 35;
	public static int CARD_MAX_DISTANCE = 72;
	public static int CARD_WIDTH = 67;
	public static int CARD_HEIGHT = 95;
	public static int CARD_VISIBLE_HEIGHT = 85;
	public static int PLAYERNAME_TEXT_DISTANCE = 5;
	public static int CARD_SELECTED_OFFSET = 15;

	private ArrayList<PlayerDecorator> _playerDecos = new ArrayList<PlayerDecorator>();
	private ArrayList<PileDecorator> _pileDecos = new ArrayList<PileDecorator>();
	private BackgroundStyle _backgroundStyle = BackgroundStyle.COLOR;
	private RGBColor _backgroundColor = new RGBColor(114, 140, 83);
	private String _backgroundTexturePath = "";

	public ArrayList<PlayerDecorator> getPlayerDecorators() {
		return _playerDecos;
	}
	public void setPlayerDecorators(ArrayList<PlayerDecorator> playerDecos) {
		_playerDecos = playerDecos;
	}
	public void addPlayerDecorator(PlayerDecorator playerDeco) {
		_playerDecos.add(playerDeco);
	}
	public PlayerDecorator getPlayerDecorator(int index) {
		return _playerDecos.get(index);
	}
	

	public ArrayList<PileDecorator> getPileDecorators() {
		return _pileDecos;
	}
	public void setPileDecorators(ArrayList<PileDecorator> pileDecos) {
		_pileDecos = pileDecos;
	}
	public void addPileDecorator(PileDecorator pileDeco) {
		_pileDecos.add(pileDeco);
	}
	public PileDecorator getPileDecorator(int index) {
		return _pileDecos.get(index);
	}
	
	public BackgroundStyle getBackgroundStyle() {
		return _backgroundStyle;
	}
	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
		_backgroundStyle = backgroundStyle;
	}
	public void setBackgroundColor(int r,int g, int b) {
		_backgroundColor = new RGBColor(r, g, b);
	}
	public RGBColor getBackgroundColor() {
		return _backgroundColor;
	}

	public String getBackgroundTexturePath() {
		return _backgroundTexturePath;
	}
	public void setBackgroundTexturePath(String backgroundTexturePath) {
		_backgroundTexturePath = backgroundTexturePath;
	}
	
	public void decorate(ViewEngine viewEngine, Board board) {
		// Decorate background
		decorateBackground(viewEngine);
		
		// Decorate piles
		for (int i = 0; i < board.getPileCount(); i++) {
			PileDecorator pileDeco = getPileDecorator(i);
			pileDeco.decorate(viewEngine, board.getPile(i));
		}
		
		// Decorate players
		for (int i = 0; i < board.getPlayerCount(); i++) {
			PlayerDecorator playerDeco = getPlayerDecorator(i);
			playerDeco.decorate(viewEngine, board.getPlayer(i));
		}
	}
	private void decorateBackground(ViewEngine viewEngine) {
		if (getBackgroundStyle().equals(BoardDecorator.BackgroundStyle.COLOR)) {
			RGBColor bgColor = getBackgroundColor();
			viewEngine.getScene().setBackground(new ColorBackground(bgColor.getRed()/256f,bgColor.getGreen()/256f,bgColor.getBlue()/256f));
		} else {
			Logger.Log("DECORATOR", "Background texture is not implemented");
		}
	}
}
