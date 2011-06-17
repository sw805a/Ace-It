package sw805a.cardgame.ui;

import java.util.Observable;
import java.util.Observer;

import sw805a.cardgame.R;
import sw805a.cardgame.comm.Client;
import sw805a.cardgame.comm.listeners.OnPlayerDisconnectedListener;
import sw805a.cardgame.domain.SyncDone;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class LoadingView extends ABaseActivity {

	private ProgressDialog cancelDialog; 

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        SyncDone.getInstance().deleteObservers();
        SyncDone.getInstance().addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				startActivity(new Intent(LoadingView.this, GameView.class));
				finish();
			}
		});
        createCancelProgressDialog("Loading", "Please wait while the game is loading", "Disconnect");		

	}
	
	@Override 
	protected void onResume() {
		setListeners();

		super.onResume();
	}
	
	@Override
	protected void onPause() {
		getGameEngine().getCommunicator().clearOnPlayerDisconnected();
		
		super.onPause();
	}
	
	
	private void setListeners() {
		// Disconnected
		getGameEngine().getCommunicator().addOnPlayerDisconnected(new OnPlayerDisconnectedListener() {
			@Override
			public void onPlayerDisconnected(Client player) {
				LoadingView.this.getGameEngine().setDisconnectedClient(player);
				LoadingView.this.getGameEngine().getCommunicator().disconnect();

		        startActivity(new Intent(LoadingView.this, PlayerDisconnectedView.class));
		    	finish();
			}
		});
	}
	
	
	private void createCancelProgressDialog(String title, String message, String buttonText)
	{
	    cancelDialog = new ProgressDialog(this);
	    cancelDialog.setTitle(title);
	    cancelDialog.setMessage(message);
	    cancelDialog.setButton(buttonText, new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	        	getGameEngine().getCommunicator().disconnect();
	        	finish();
	        }
	    });
	    cancelDialog.show();
	}

}
