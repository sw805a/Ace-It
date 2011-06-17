package sw805a.cardgame.comm.bluetooth;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import flexjson.JSONSerializer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import sw805a.cardgame.ApplicationController;
import sw805a.cardgame.comm.AGameCommunication;
import sw805a.cardgame.comm.Client;
import sw805a.cardgame.logger.Logger;


public class BluetoothGameCommunication extends AGameCommunication {
	
	// Service name and UUID
	public static final String SERVICE_NAME = "CardGame";
	
	public static final UUID CONNECT_UUID =
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000000000");
	
	public static final UUID[] SERVICE_UUID = {
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000010000"),
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000010001"),
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000010002"),
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000010003"),
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000010004"),
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000010005"),
		UUID.fromString("fa87c0d0-afac-11de-8a39-080000010006")
	};

	// Bluetooth adapter
	private final BluetoothAdapter _adapter = BluetoothAdapter.getDefaultAdapter();
	
	// Lists
	private CopyOnWriteArrayList<BluetoothPlayer> _connectedPlayers = new CopyOnWriteArrayList<BluetoothPlayer>();

	// Threads
	private AcceptThread _acceptThread;
	private ArrayList<ConnectThread> _connectThreads = new ArrayList<BluetoothGameCommunication.ConnectThread>();
	
	// Loop variables
	private boolean _isConnected;
	
	private int _inviteeCount = 0, _invitedCount = 0;
	
	private boolean _isHost = false;
	
	@Override
	public void enterLobby() {
		Logger.Log("enter lobby");
		
		for (ConnectThread c : _connectThreads) {
			c.cancel();
		}
		_connectThreads.clear();
		
		startBluetoothDiscoverty();
		startBluetoothAcceptThread();
	}
	
	@Override
	public void leaveLobby() {
		if (_acceptThread != null) {
			_acceptThread.cancel();
			_acceptThread = null;
        }
		
		_adapter.cancelDiscovery();
		_isHost = false;
	}

	public void setInviteeCount(int size) {
		_inviteeCount = size;
		_invitedCount = 0;
		_isHost = true;
	}
	
	@Override
	public void invite(Client target) throws Exception {
		if (_inviteeCount == 0) throw new Exception("Invitee count not set");
		BluetoothPlayer player = castToBluetoothPlayer(target);
		ConnectThread cThread = new ConnectThread(player, _invitedCount);
		_connectThreads.add(cThread);
		cThread.start();
		cThread.invite();
		_invitedCount++;
	}

	@Override
	public void disconnect() {
		_isConnected = false;
		for(BluetoothPlayer player : _connectedPlayers) {
			BluetoothSocket socket = player.getSocket();
			if(socket == null) Logger.Log("SOCKET IS NULL!! (Disconnect method)");
			try {
				socket.close();
			} catch (IOException e) {
				Toast.makeText(ApplicationController.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void sendMessage(Object msg) {
		JSONSerializer s = new JSONSerializer();
		String strMsg = s.serialize(msg);
		
		for(BluetoothPlayer player : (CopyOnWriteArrayList<BluetoothPlayer>)_connectedPlayers.clone()) {
			sendMessage(player, strMsg);
		}
	}
	
	@Override
	public void sendMessage(Client target, Object msg) {
		JSONSerializer s = new JSONSerializer();
		String strMsg = s.serialize(msg);		
		sendMessage(target, strMsg);
	}
	
	private void sendMessage(Client target, String serializedMessage) {
		BluetoothPlayer player = castToBluetoothPlayer(target);
		BluetoothSocket socket = player.getSocket();
		if(socket == null) Logger.Log("SOCKET IS NULL!! (SendMessage method)");
		try {
			socket.getOutputStream().write(((serializedMessage + '\0').getBytes()));
		} catch (IOException e) {
			_connectedPlayers.remove(player);
			notifyPlayerDisconnected(player);
		}
	}	
	
	
	private void startBluetoothDiscoverty(){
		_lobby.clear();
		
		Set<BluetoothDevice> pairedDevices = _adapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
	        	updateLobby(device);
	            notifyLobbyUpdated();
		    }
		}
		
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
			        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        	updateLobby(device);
		            notifyLobbyUpdated();
		        }
		        if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
					_adapter.startDiscovery();
		        }
		    }
		};
		
		// Register the BroadcastReceiver if we are not already discovering
		if (!_adapter.isDiscovering()) {
			Logger.Log("discovering...");
			IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			IntentFilter finishFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			ApplicationController.getContext().registerReceiver(receiver, foundFilter);
			ApplicationController.getContext().registerReceiver(receiver, finishFilter);

			_adapter.startDiscovery();
		} else {
			Logger.Log("continue loldiscovering");
		}
	}
	
	private void startBluetoothAcceptThread(){
		if (_acceptThread != null) {
			_acceptThread.cancel();
			_acceptThread = null;
        }
		
		_acceptThread = new AcceptThread();
		_acceptThread.start();
	}
	
	public void startBluetoothReadThread(BluetoothPlayer player) {
		new ReadThread(player).start();
	}		
	
	
	
	// TRÅDENE ER HERFRA: http://developer.android.com/resources/samples/BluetoothChat/src/com/example/android/BluetoothChat/BluetoothChatService.html
	
	/**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
	private class AcceptThread extends Thread {
		private BluetoothServerSocket _serverSocket;
		
		public AcceptThread() {
			super("Accept thread: ");
			try {
				_serverSocket = _adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME+": Connect", CONNECT_UUID);
			} catch (IOException e) {

			}
			Logger.Log("initiating accept thread constructor");
		}		
		
		@Override
		public void run() {
			BluetoothSocket helloSocket = null,commSocket = null; 
			Logger.Log("starting accept thread");
			try {
                // This is a blocking call and will only return on a
                // successful connection or an exception	
				helloSocket = _serverSocket.accept();
            } catch (IOException e) {

            }

            // If a connection was accepted
            if (helloSocket != null) {
            	// Then read the actual server socket to use and connect to it
            	InputStream is = null;
            	OutputStream os = null;
				try {
					is = helloSocket.getInputStream();
					os = helloSocket.getOutputStream();
				} catch (IOException e) {

					return;
				}
				
				try {
					int theConnectionToUse = is.read();
					os.write(1);// We got it, byes
					BluetoothDevice dev = helloSocket.getRemoteDevice();
					helloSocket.close();
					commSocket = dev.createRfcommSocketToServiceRecord(SERVICE_UUID[theConnectionToUse]);
					commSocket.connect();
				} catch (IOException e) {

					return;
				}
				
            	if (commSocket != null) {
	            	// find the client in the lobby list
	            	BluetoothPlayer btplayer = updateLobby(commSocket.getRemoteDevice());
					btplayer.setSocket(commSocket);
					_connectedPlayers.addIfAbsent(btplayer);
					notifyPlayerConnected(btplayer);
					startBluetoothReadThread(btplayer);
            	}
			}
			_acceptThread = null;
		}
		
        public void cancel() {
        	try {
        		if (_serverSocket != null) {
        			_serverSocket.close();
        		}
            } catch (IOException e) {
            	
            	return;
            }
        }
	}

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming.
     */
	private class ReadThread extends Thread {
        private InputStream _inStream;
        private BluetoothSocket _socket;
        private BluetoothPlayer _player;
        
        public ReadThread(BluetoothPlayer player) {
        	_player = player;
        	_socket = player.getSocket();
        	if(_socket == null) {
        		Logger.Log("BT","Socket was null?! What the heck! Should have been set in player. (Msg from Read Thread)");
        	}
        	
            // Get the BluetoothSocket input and output streams
            try {
            	_inStream = _socket.getInputStream();
            } catch (IOException e) {
            	
                notifyPlayerDisconnected(_player);
            }

            _isConnected = true; // Started to listen
        }

        public void run() {
        	int input;
        	StringBuilder msg;
        	
            // Keep listening to the InputStream while connected
            while (true) {
            	msg = new StringBuilder();
				try {
					do {
						input = _inStream.read();
						msg.append((char)input);
					} while(input != '\0');
					
					notifyMessage(_player, new String(msg).trim());

				} catch (IOException e) {
					
                	
                	if (reestablishConnection(_player)) {
                		try {
							_inStream = _player.getSocket().getInputStream();
						} catch (IOException e1) {

							break;
						}
                	} else {
                		break;	
                	}
                }
            }
            _connectedPlayers.remove(_player);
            if(_isConnected) {
            	notifyPlayerDisconnected(_player);
            	_isConnected = false;
            }
        }
        
        @SuppressWarnings("unused")
		public void cancel() {
            try {
                _socket.close();
            } catch (IOException e) {
            	
            	return;
            }
        }        
	}
	
	
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */	
	private class ConnectThread extends Thread {
		private BluetoothServerSocket _serverSocket;
		private BluetoothSocket _socket;
        private BluetoothPlayer _player;

        private int _id;
        
        public ConnectThread(BluetoothPlayer player, int id) {
        	super("Connect thread: " + SERVICE_UUID[id].toString());
        	_player = player;
        	_player.setId(id);
        	_id = id;
        	
            try {
            	_serverSocket = _adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID[id]);
            } catch (IOException e) {
            	
            	return;
            }
        }

        public void run() {
            // Always cancel discovery because it will slow down a connection
            _adapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                _socket = _serverSocket.accept();
            } catch (IOException e) {
            	
                try {
                    _socket.close();
                } catch (IOException e2) {
                	
                }
                
                notifyPlayerDisconnected(_player);
                return;
            }

            // save the socket with the player
            _player.setSocket(_socket);
            
            // Reset the ConnectThread because we're done
            synchronized (BluetoothGameCommunication.this) {
                _connectThreads.remove(this);
            }

            _connectedPlayers.addIfAbsent(_player);
            startBluetoothReadThread(_player);
            notifyPlayerConnected(_player);
        }

        public void invite() {
        	try {
        		_socket = _player.getDevice().createRfcommSocketToServiceRecord(CONNECT_UUID);
				_socket.connect();
	            _socket.getOutputStream().write(_id);
	            _socket.getInputStream().read();
	            _socket.close();
	            try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {

				}
			} catch (IOException e) {
				
				return;
			}
        }
        
        @SuppressWarnings("unused")
		public void cancel() {
        	try {
        	_serverSocket.close();
        	} catch (IOException e) {
        		Logger.Log("ConnectThread close() socket failed: "+e.getMessage());
        	}
            try {
            	
                _socket.close();
            } catch (IOException e) {
                Logger.Log("ConnectThread close() socket failed: "+e.getMessage());
            }
        }
    }

	
	private BluetoothPlayer updateLobby(BluetoothDevice device) {
		for(Client client : _lobby) {
        	if(client.getAddress().equals(device.getAddress()))
        	{
        		if(!device.getName().equals(""))
        			client.setName(device.getName());
        		
        		return (BluetoothPlayer)client;
        	}
        }
        
        BluetoothPlayer player = new BluetoothPlayer(device);
		if(!device.getName().equals(""))
			_lobby.add(player);

		return player;
	}


	public boolean reestablishConnection(BluetoothPlayer player) {
		int id = player.getId();
		if (_isHost) {
			try {
				BluetoothServerSocket bss = _adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME+id, SERVICE_UUID[id]);
				BluetoothSocket bs = bss.accept(10000);
				player.setSocket(bs);
				return true;
			} catch (Exception e) {
				
				return false;
			}
			
		} else {
			try {
				BluetoothSocket bs = player.getSocket().getRemoteDevice().createRfcommSocketToServiceRecord(SERVICE_UUID[id]);
				bs.connect();
				player.setSocket(bs);
				return true;
			} catch (Exception e) {
				
				return false;
			}
		}
	}

	private BluetoothPlayer castToBluetoothPlayer(Client player){
		if(player instanceof BluetoothPlayer){
			return (BluetoothPlayer)player;
		} else {
			for (BluetoothPlayer p : _connectedPlayers) {
				if(p.getAddress().equals(player.getAddress())){
					return p;
				}
			}
			for(Client p : _lobby){
				if(p.getAddress().equals(player.getAddress())){
					return (BluetoothPlayer)p;
				}
			}
		}
		
		Logger.Log("player was from outer space");
		return null;
	}

	public String getMyAddress() {
		return _adapter.getAddress();
	}

	public String getMyName() {
		return _adapter.getName();
	}

	@Override
	public int getInviteeCount() {
		return _inviteeCount;
	}

	@Override
	public int getInvitedCount() {
		return _invitedCount;
	}
}
