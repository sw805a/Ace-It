package sw805a.cardgame.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sw805a.cardgame.R;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import sw805a.cardgame.comm.*;
import sw805a.cardgame.comm.listeners.OnLobbyUpdatedListener;
import sw805a.cardgame.comm.listeners.OnMessageListener;
import sw805a.cardgame.domain.Invitation;
import sw805a.cardgame.domain.SyncDone;

public class LobbyView extends ABaseActivity {
	
	
	public class ClientListItem {
		public boolean Checked = false;
		public Client Client = null;
		
		public ClientListItem(Client client) {
			Client = client;
		}
	}
	
    private ListView _clientList;
        
    private static ArrayList<ClientListItem> _discoveredClients = null;
    private ClientAdapter _clientAdapter;
    

    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
        
        _clientList = (ListView)this.findViewById(R.id.playerList);
        
        if (_discoveredClients == null) {
        	_discoveredClients = new ArrayList<ClientListItem>();
        }

        _clientAdapter = new ClientAdapter(LobbyView.this, R.layout.lobby_client,_discoveredClients);
        _clientList.setAdapter(_clientAdapter);
        
        SyncDone.getInstance().deleteObservers();
        SyncDone.getInstance().addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				startActivity(new Intent(LobbyView.this, GameView.class));
			}
		});
	}
	
	
	@Override
	public void onResume() {
        setListeners();
        getGameEngine().getCommunicator().enterLobby();
		_clientAdapter.notifyDataSetChanged();
		Button invite = (Button)findViewById(R.id.lobbyViewInvite);
        if(getCheckedClients().size() > 0) {
        	invite.setEnabled(true);
        } else {
        	invite.setEnabled(false);
        }		

        super.onResume();
	}
	
	@Override
	protected void onPause() {
		removeListeners();
        getGameEngine().getCommunicator().leaveLobby();
		
        super.onPause();
	}
	
	private void setListeners() {

		getGameEngine().getCommunicator().addOnLobbyUpdated(new OnLobbyUpdatedListener() {
			
			@Override
			public void onLobbyUpdated(List<Client> players) {
				for(Client client : players){
					boolean found = false;
					for (ClientListItem li : _discoveredClients) {
						if (li.Client.getAddress().equals(client.getAddress())) {
							found = true;
							break;
						}
					}
					if (!found) {
						_discoveredClients.add(new ClientListItem(client));
						_clientAdapter.notifyDataSetChanged();							
					}
				}
			}
		});
		
		
		getGameEngine().getCommunicator().addMessageListener(Invitation.class, new OnMessageListener() {
			@Override
			public void OnMessage(Client sender, Object message) {
				AlertDialog.Builder builder = new AlertDialog.Builder(LobbyView.this);
				builder.setTitle(R.string.InvitationTitle);
				builder.setMessage(String.format(getResources().getString(R.string.InvitationText), sender.getName(), "R�vhul"));
				builder.setCancelable(false);
				builder.setPositiveButton(getResources().getString(R.string.Accept), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						LobbyView.this.getGameEngine().getCommunicator().leaveLobby();
						LobbyView.this.getGameEngine().answerInvite(true);
				        startActivityForResult(new Intent(LobbyView.this, LoadingView.class),0);
					}
				});
				builder.setNegativeButton(getResources().getString(R.string.Reject), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LobbyView.this.getGameEngine().answerInvite(false);
						LobbyView.this.getGameEngine().getCommunicator().leaveLobby();
						LobbyView.this.getGameEngine().getCommunicator().enterLobby();
						
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			};
		});
		
		
        // Client list
        _clientList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				_clientAdapter.getItem(position).Checked = !_clientAdapter.getItem(position).Checked; 
				_clientAdapter.notifyDataSetChanged();
				
				Button invite = (Button)findViewById(R.id.lobbyViewInvite);
		        if(getCheckedClients().size() > 0) {
		        	invite.setEnabled(true);
		        } else {
		        	invite.setEnabled(false);
		        }			
			}
		});

        
        // Invite button
        Button invite = (Button)findViewById(R.id.lobbyViewInvite);
        
        invite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {		
		        ArrayList<Client> clients = getCheckedClients();
		        getGameEngine().getCommunicator().leaveLobby();
		        getGameEngine().setClients(clients);
		        startActivity(new Intent(LobbyView.this, WaitingForPlayersView.class));
			}
        	
        });
	}
	

	private ArrayList<Client> getCheckedClients() {
    	ArrayList<Client> checkedClients = new ArrayList<Client>();
    	
    	for (ClientListItem li : _discoveredClients) {
    		if (li.Checked)
    			checkedClients.add(li.Client);
    	}
    	
    	return checkedClients;
    }
	
	private void removeListeners() {
		getGameEngine().getCommunicator().clearOnLobbyUpdated();
		getGameEngine().getCommunicator().removeMessageListeners(Invitation.class);		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
