package sw805a.cardgame.ui;

import sw805a.cardgame.game.GameEngine;
import sw805a.cardgame.game.IGameEngine;
import android.app.Activity;

public abstract class ABaseActivity extends Activity {
    protected IGameEngine getGameEngine() {
    	return GameEngine.getInstance();
    }
}
