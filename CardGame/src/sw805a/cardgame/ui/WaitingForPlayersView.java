package sw805a.cardgame.ui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import sw805a.cardgame.R;
import sw805a.cardgame.comm.*;
import sw805a.cardgame.comm.listeners.OnMessageListener;
import sw805a.cardgame.comm.listeners.OnPlayerDisconnectedListener;
import sw805a.cardgame.domain.InvitationAccepted;
import sw805a.cardgame.domain.SyncDone;

public class WaitingForPlayersView extends ABaseActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_for_players);
        
        SyncDone.getInstance().deleteObservers();
        SyncDone.getInstance().addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				startActivity(new Intent(WaitingForPlayersView.this, GameView.class));
				finish();
			}
		});
        
        getGameEngine().inviteClients();
        UpdateLists();
        
	}

	
	@Override
	protected void onResume() {
		setListeners();

		super.onResume();
	}
	
	@Override
	protected void onPause() {
		removeListeners();
		
		super.onPause();
	}
	
	
	private void setListeners() {

		// On accepted
		getGameEngine().getCommunicator().addMessageListener(InvitationAccepted.class, new OnMessageListener() {

			@Override
			public void OnMessage(Client sender, Object message) {
				if (getGameEngine().getCommunicator().getInvitedCount() == getGameEngine().getCommunicator().getInviteeCount()) {
					startActivity(new Intent(WaitingForPlayersView.this, LoadingView.class));
					finish();
				}
			}
			
		});
		
		// Disconnected
		getGameEngine().getCommunicator().addOnPlayerDisconnected(new OnPlayerDisconnectedListener() {
			@Override
			public void onPlayerDisconnected(Client player) {
				WaitingForPlayersView.this.getGameEngine().setDisconnectedClient(player);
				WaitingForPlayersView.this.getGameEngine().getCommunicator().disconnect();

		        startActivity(new Intent(WaitingForPlayersView.this, PlayerDisconnectedView.class));

		        finish();
			}
		});
			

		// Cancel button
		Button cancelButton = (Button)findViewById(R.id.waitingForPlayersCancel);
        cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WaitingForPlayersView.this.getGameEngine().getCommunicator().disconnect();

				finish();
			}
        });
        
	}
	
	private void removeListeners() {
		getGameEngine().getCommunicator().clearOnPlayerDisconnected();
		getGameEngine().getCommunicator().removeMessageListeners(InvitationAccepted.class);
	}
	

	private void UpdateLists()
	{
		ListView waiting = (ListView)findViewById(R.id.playerListWaiting);
		ArrayList<String> playerNames = new ArrayList<String>();
		
		for (Client player : getGameEngine().getClients()) {
			playerNames.add(player.getName());
		}
		
		waiting.setAdapter((ListAdapter)new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames));
	}
}
