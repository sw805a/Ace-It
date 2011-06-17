package sw805a.cardgame.game.models;

import java.util.ArrayList;

public class Hand extends ACardHolder {
	public Hand() {}
	public Hand(Player player, ArrayList<Card> cards) {
		addCards(player, cards);
	}
	
}
