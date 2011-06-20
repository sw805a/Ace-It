package sw805a.cardgame.ui;

import sw805a.cardgame.R;
import sw805a.cardgame.comm.Client;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.*;

public class PlayerDisconnectedView extends ABaseActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.player_disconnected);
        
        
        TextView player = (TextView)findViewById(R.id.playerDisconnectedName);
        
        Client p = this.getGameEngine().getDisconnectedClient();
        player.setText(player != null ? p.getName() : "Unknown");
        
        
        Button ok = (Button)findViewById(R.id.playerDisconnectedButton);
        ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        //startActivity(new Intent(PlayerDisconnectedView.this, LobbyView.class));
		    	finish();
			}
        	
        });
	}
}
