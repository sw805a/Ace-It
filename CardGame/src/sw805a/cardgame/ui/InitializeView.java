package sw805a.cardgame.ui;

import sw805a.cardgame.R;
import android.content.Intent;
import android.os.Bundle;

public class InitializeView extends ABaseActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialize_view);
        
        getGameEngine().hardReset();
        
        startActivity(new Intent(this, CommunicationSelectView.class));
        finish();
	}

}
