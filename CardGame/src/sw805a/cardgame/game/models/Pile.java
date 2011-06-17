package sw805a.cardgame.game.models;


import flexjson.JSON;
import sw805a.cardgame.game.models.MoveHistory.MoveDirection;

public class Pile extends ACardHolder {
	public Pile() {}
	
	@JSON(include=false)
	public Card getTopCard() {
		return _cards.get(_cards.size() - 1);
	}
	
	@JSON(include=false)
	public Card getTopCard(int offset) {
		return _cards.get(_cards.size() - 1 - offset);
	}
	
	@JSON(include=false)
	public MoveHistory getLastAddMove() {
		if (getMoveHistory().size() != 0) {
			for (int i = getMoveHistory().size() - 1; i >= 0; i--) {
				if (getMoveHistory().get(i).getDirection() == MoveDirection.ADD) {
					return getMoveHistory().get(i);
				}
			}
		}
		return null;
	}
}
