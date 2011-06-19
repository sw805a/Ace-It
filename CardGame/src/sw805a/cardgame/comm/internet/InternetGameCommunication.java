package sw805a.cardgame.comm.internet;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;


import sw805a.cardgame.ApplicationController;
import sw805a.cardgame.comm.AGameCommunication;
import sw805a.cardgame.comm.Client;
import sw805a.cardgame.comm.internet.messages.LobbyUpdate;
import sw805a.cardgame.logger.Logger;


public class InternetGameCommunication extends AGameCommunication {
	
	// Lists
	private CopyOnWriteArrayList<InternetPlayer> _connectedPlayers = new CopyOnWriteArrayList<InternetPlayer>();

	// Threads
	private AcceptThread _acceptThread;
	private ArrayList<ConnectThread> _connectThreads = new ArrayList<ConnectThread>();
	
	private InternetLobby _lobbyThread = null;
	
	// Loop variables
	private boolean _isAccepting;
	private boolean _isConnected;
	
	@Override
	public void enterLobby() {
		Logger.Log("enter lobby");
		
		startInternetDiscoverty();
		startInternetAcceptThread();
	}
	
	@Override
	public void leaveLobby() {
		if (_acceptThread != null) {
			_acceptThread.cancel();
			//_acceptThread = null;
        }
		if (_lobbyThread != null) {
			_lobbyThread.cancel();
			//_lobbyThread = null;
		}
	}

	private int _inviteeCount = 0,_invitedCount = 0;
	@Override
	public void invite(Client target) throws Exception {
		if (_inviteeCount == 0) throw new Exception("Invitee count not set");
		InternetPlayer player = castToInternetPlayer(target);
		ConnectThread cThread = new ConnectThread(player);
		_connectThreads.add(cThread);
		cThread.start();
		_invitedCount++;

	}

	@Override
	public void disconnect() {
		_isConnected = false;
		for(InternetPlayer player : _connectedPlayers) {
			Socket socket = player.getSocket();
			if(socket == null) Logger.Log("SOCKET IS NULL!! (Disconnect method)");
			try {
				socket.close();
			} catch (IOException e) {
				Logger.Log("Could not close bluetooth socket (Disconnect method)");
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void sendMessage(Object msg) {
		JSONSerializer s = new JSONSerializer();
		String strMsg = s.serialize(msg);
		
		for(InternetPlayer player : (CopyOnWriteArrayList<InternetPlayer>)_connectedPlayers.clone()) {
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
		InternetPlayer player = castToInternetPlayer(target);
		Socket socket = player.getSocket();
		if(socket == null) Logger.Log("SOCKET IS NULL!! (SendMessage method)");
		try {
			socket.getOutputStream().write(((serializedMessage + '\0').getBytes()));
		} catch (IOException e) {
			_connectedPlayers.remove(player);
			notifyPlayerDisconnected(player);
		}
	}	
	
	
	private void startInternetDiscoverty(){
		_lobby.clear();
		
		if (_lobbyThread == null) {
			_lobbyThread = new InternetLobby();
			_lobbyThread.start();
		}
		/*
		updateLobby(new InternetPlayer("172.25.18.104", "Heine 58"));
		updateLobby(new InternetPlayer("172.25.19.158", "Jon"));
		updateLobby(new InternetPlayer("172.25.19.75", "Torben 84"));
		notifyLobbyUpdated();
		*/
	}
	
	private void startInternetAcceptThread(){
		if (_acceptThread != null) {
			_acceptThread.cancel();
			_acceptThread = null;
        }
		
		_acceptThread = new AcceptThread();
		_acceptThread.start();
	}
	
	public void startBluetoothReadThread(InternetPlayer player) {
		new ReadThread(player).start();
	}		
	
	
	
	// TR�DENE ER HERFRA: http://developer.android.com/resources/samples/BluetoothChat/src/com/example/android/BluetoothChat/BluetoothChatService.html
	
	/**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
	private String a;
	private class AcceptThread extends Thread {

		private Socket _socket;
		private ServerSocket _serverSocket;
		
		public AcceptThread() {
			_isAccepting = true;
			Logger.Log("initiating accept thread constructor");
            // Create a new listening server socket
            try {
                _serverSocket = new ServerSocket(20000);
            } catch (IOException e) {
            	a = e.getMessage();
            	Logger.Log("BluetoothServerThread listen() failed"+a);
            }
        }		
		
		@Override
		public void run() {
			
			while (_isAccepting) {
	            Logger.Log("starting accept thread");
				try {
	                // This is a blocking call and will only return on a
	                // successful connection or an exception
	            	_socket = _serverSocket.accept();
	            } catch (IOException e) {
	                Logger.Log("BluetoothServerThread accept() failed");
	                break;
	            }
	            Logger.Log("connection accepted from accept thread");

	            // If a connection was accepted
	            if (_socket != null) {
	            	// find the client in the lobby list
	            	InternetPlayer inetplayer = updateLobby(new InternetPlayer(_socket.getLocalAddress().toString(),""));
					inetplayer.setSocket(_socket);
					_connectedPlayers.addIfAbsent(inetplayer);
					notifyPlayerConnected(inetplayer);
					startBluetoothReadThread(inetplayer);
					_isAccepting = false; // TODO : implement multiple connections
	            }
	        }	
		}
		
        public void cancel() {
    		_isAccepting = false;
        	try {
                _serverSocket.close();
            } catch (IOException e) {
                Logger.Log("socket close() of server failed"+ e.getMessage());
            }
        }
	}

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming.
     */
	private class ReadThread extends Thread {
        private InputStream _inStream;
        private Socket _socket;
        private InternetPlayer _player;
        
        public ReadThread(InternetPlayer player) {
        	setName("ReadThread");
        	_player = player;
        	_socket = player.getSocket();
        	
        	if(_socket == null) {
        		Logger.Log("Socket was null?! What the heck! Should have been set in player. (Msg from Read Thread)");
        	}
        	
            // Get the BluetoothSocket input and output streams
            try {
            	_inStream = _socket.getInputStream();
            } catch (IOException e) {
                Logger.Log("ReadThread failed to create streams: "+e.getMessage());
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
                	Logger.Log("client disconnected: "+e.getMessage());
                    break;
                }
            }
            _connectedPlayers.remove(_player);
            if(_isConnected) {
            	notifyPlayerDisconnected(_player);
            	_isConnected = false;
            }
        }
        
        public void cancel() {
            try {
                _socket.close();
            } catch (IOException e) {
                Logger.Log("close() of connect socket failed"+ e.getMessage());
            }
        }        
	}
	
	
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */	
	private class ConnectThread extends Thread {
		private final Socket _socket;
        private final InternetPlayer _player;

        public ConnectThread(InternetPlayer player) {
        	_player = player;

        	Socket tmp = null;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
                try {
					tmp = new Socket(player.getAddress(), 20000);
				} catch (UnknownHostException e) {
					a = e.getMessage();
				} catch (IOException e) {
					a = e.getMessage();
				}
    
            _socket = tmp;
        }

        public void run() {

            // Always cancel discovery because it will slow down a connection

            // Make a connection to the BluetoothSocket
            // save the socket with the player
            _player.setSocket(_socket);
            
            // Reset the ConnectThread because we're done
            synchronized (InternetGameCommunication.this) {
                _connectThreads.remove(this);
            }

            _connectedPlayers.addIfAbsent(_player);
            startBluetoothReadThread(_player);
            notifyPlayerConnected(_player);
        }

        public void cancel() {
            try {
                _socket.close();
            } catch (IOException e) {
                Logger.Log("ConnectThread close() socket failed: "+e.getMessage());
            }
        }
    }

	
	private InternetPlayer updateLobby(InternetPlayer player) {
		for(Client client : _lobby) {
        	if(client.getAddress().equals(player.getAddress()))
        	{
        		
        		return (InternetPlayer)client;
        	}
        }
        
		_lobby.add(new InternetPlayer(player.getAddress(), player.getName()));

		return player;
	}


	private InternetPlayer castToInternetPlayer(Client player){
		if(player instanceof InternetPlayer){
			return (InternetPlayer)player;
		} else {
			for (InternetPlayer p : _connectedPlayers) {
				if(p.getAddress().equals(player.getAddress())){
					return p;
				}
			}
			for(Client p : _lobby){
				if(p.getAddress().equals(player.getAddress())){
					return (InternetPlayer)p;
				}
			}
		}
		
		Logger.Log("player was from outer space");
		return null;
	}

	public String getMyAddress() {
		return getLocalIpAddress();
	}

	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	    }
	    return null;
	}
	
	public String getMyName() {
		return "Bob";
	}

	@Override
	public void setInviteeCount(int size) {
		_inviteeCount = size;
		_invitedCount = 0;
	}

	@Override
	public int getInviteeCount() {
		return _inviteeCount;
	}
	@Override
	public int getInvitedCount() {
		return _invitedCount;
	}
	public class InternetLobby extends Thread {
		Socket mSocket;
		public void run() {
			try {
				SharedPreferences prefs = ApplicationController.getContext().getSharedPreferences("CardGame", 0);
				mSocket = new Socket(prefs.getString("ip", "nobelnet.dk"),8002);
				InputStream inStream = mSocket.getInputStream();
				while (true) {
					StringBuilder msg = new StringBuilder();
					int input;
					try {
						do {
							input = inStream.read();
							msg.append((char)input);
						} while(input != '\0');
						JSONDeserializer<Object> jd = new JSONDeserializer<Object>();
						Object object = jd.deserialize(msg.toString());
						if (object.getClass().equals(LobbyUpdate.class)) {
							LobbyUpdate update = (LobbyUpdate) object;
							updateLobby(new InternetPlayer(update.getAddress(),update.getName()));
							notifyLobbyUpdated();
						}
					} catch (IOException e) {
						Logger.Log(e.getMessage());
						
		                break;
		            }
				}
			} catch (UnknownHostException e) {

			} catch (IOException e) {
				
			}
		}
		public void cancel() {
			try {
				if(mSocket != null)
					mSocket.close();
			} catch (IOException e) {

			}
		}
	}
}
