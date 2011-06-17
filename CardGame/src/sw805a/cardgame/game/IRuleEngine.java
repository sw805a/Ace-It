package sw805a.cardgame.game;

import java.util.ArrayList;

import sw805a.cardgame.game.models.Card;
import sw805a.cardgame.game.models.GameState;
import sw805a.cardgame.game.models.Move;
import sw805a.cardgame.game.models.Player;
import sw805a.cardgame.ui.decorators.BoardDecorator;


public interface IRuleEngine {

	// Init
	public void initializeGame(Player myPlayer, ArrayList<Player> players);

	// Decorator
	public BoardDecorator getBoardDecorator();

	// Sync
	public GameState getGameState();
	public void receiveGameState(Player myPlayer, GameState gameState);

	// Actons
	public void selectCard(Card card);
	public void recieveMove(Move move);
	public Move makeMove();
}

