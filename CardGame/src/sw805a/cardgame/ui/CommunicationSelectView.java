package sw805a.cardgame.ui;

import sw805a.cardgame.ApplicationController;
import sw805a.cardgame.R;
import sw805a.cardgame.comm.CommunicationType;
import sw805a.cardgame.game.GameEngine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class CommunicationSelectView extends ABaseActivity {
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.comm_select);
        
        GameEngine.getInstance().hardReset();
        
        Button bluetooth = (Button)findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getGameEngine().setCommunicationType(CommunicationType.BLUETOOTH);
				startActivity(new Intent(CommunicationSelectView.this, GameSelectView.class));
			}
		});
        
        Button internet = (Button)findViewById(R.id.internet);
        internet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getGameEngine().setCommunicationType(CommunicationType.INTERNET);
				startActivity(new Intent(CommunicationSelectView.this, GameSelectView.class));
			}
		});
        
        Button settings = (Button)findViewById(R.id.settings);
        settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(CommunicationSelectView.this);

				alert.setTitle("Internet Lobby");
				alert.setMessage("Enter Lobby Address");

				// Set an EditText view to get user input 
				final EditText input = new EditText(CommunicationSelectView.this);
				alert.setView(input);


				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
				  	SharedPreferences prefs = ApplicationController.getContext().getSharedPreferences("CardGame", 0);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("ip", value);
					editor.commit();
				  }
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});

				alert.show();
			}
		});
	}
	
	@Override
	public void onResume() {
        super.onResume();
	}
	
	@Override
	protected void onPause() {
		
        super.onPause();
	}
}
