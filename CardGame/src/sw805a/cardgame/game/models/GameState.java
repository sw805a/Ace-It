package sw805a.cardgame.game.models;

import flexjson.JSON;



public class GameState {
	private Board _board = new Board();
	
	
	@JSON
	public Board getBoard() {
		return _board;
	}
	public void setBoard(Board board) {
		_board = board;
	}
	
}
