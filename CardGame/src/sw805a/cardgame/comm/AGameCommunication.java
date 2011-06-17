package sw805a.cardgame.comm;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import flexjson.JSONDeserializer;
import android.os.Handler;
import android.os.Message;
import sw805a.cardgame.comm.listeners.*;

public abstract class AGameCommunication implements IGameCommunication {

	// use the thread safe array to ensure that the listeners aren't removed while we are using them

	private Handler _lobbyUpdatedHandler, _playerDisconnectedHandler, _playerConnectedHandler, _messageHandler;

	private class MessageListenerClass
	{
		private OnMessageListener _listener;
		@SuppressWarnings("rawtypes")
		private Class _clazz;
		@SuppressWarnings("rawtypes")
		public MessageListenerClass(OnMessageListener listener, Class clazz) {
			_clazz = clazz;
			_listener = listener;
		}
		
		public OnMessageListener getListener() {
			return _listener;
		}
		@SuppressWarnings("rawtypes")
		public Class getClazz() {
			return _clazz;
		}
		
	}
	
	
	private CopyOnWriteArrayList<OnLobbyUpdatedListener> _lobbyUpdated = new CopyOnWriteArrayList<OnLobbyUpdatedListener>();
	private CopyOnWriteArrayList<OnPlayerDisconnectedListener> _playerDisconnected = new CopyOnWriteArrayList<OnPlayerDisconnectedListener>();
	private CopyOnWriteArrayList<OnPlayerConnectedListener> _playerConnected = new CopyOnWriteArrayList<OnPlayerConnectedListener>();
	private CopyOnWriteArrayList<MessageListenerClass> _messageListeners = new CopyOnWriteArrayList<MessageListenerClass>();	
	protected CopyOnWriteArrayList<Client> _lobby = new CopyOnWriteArrayList<Client>();	
	
	class MessageWithSender {
		private Client _sender;
		private Object _message;
		public MessageWithSender(Client sender, Object message) {
			_message = message;
			_sender = sender;
		}
		
		public Client getSender() {
			return _sender;
		}
		public Object getMessage() {
			return _message;
		}
	}
	
	public AGameCommunication() {
		_lobbyUpdatedHandler = new Handler() {
			public void handleMessage(Message message) {
				@SuppressWarnings("unchecked")
				List<Client> players = (List<Client>)message.obj;
				for(OnLobbyUpdatedListener listener : _lobbyUpdated) {
					listener.onLobbyUpdated(players);
				}
			}
		};
		
		_playerDisconnectedHandler = new Handler() {
			public void handleMessage(Message message) {
				Client player = (Client)message.obj;
				for(OnPlayerDisconnectedListener listener : _playerDisconnected) {
					listener.onPlayerDisconnected(player);
				}
			}
		};
		
		_playerConnectedHandler = new Handler() {
			public void handleMessage(Message message) {
				Client player = (Client)message.obj;
				for(OnPlayerConnectedListener listener : _playerConnected) {
					listener.onConnect(player);
				}

			}
		};
		_messageHandler = new Handler() {
			public void handleMessage(Message message) {
				MessageWithSender messageWithSender = (MessageWithSender)message.obj;
				
				Object msg = messageWithSender.getMessage();
				Client sender = messageWithSender.getSender();
				
				for(MessageListenerClass listener : _messageListeners) {

					// if the listener is listening for this type - give it to him (invoke his/her method)
			        if(listener.getClazz().equals(msg.getClass())) {
		        		listener.getListener().OnMessage(sender, msg);
			        }
				}
			}
		};
	}
	
	protected void notifyLobbyUpdated() {
		Message msg = _lobbyUpdatedHandler.obtainMessage();
		msg.obj = _lobby.clone();
		_lobbyUpdatedHandler.sendMessage(msg);
	}

	protected void notifyPlayerDisconnected(Client player) {
		Message msg = _playerDisconnectedHandler.obtainMessage();
		msg.obj = player;
		_playerDisconnectedHandler.sendMessage(msg);
	}	

	protected void notifyPlayerConnected(Client player) {
		Message msg = _playerConnectedHandler.obtainMessage();
		msg.obj = player;
		_playerConnectedHandler.sendMessage(msg);
	}


	protected void notifyMessage(Client sender, String message) {
		JSONDeserializer<Object> jd = new JSONDeserializer<Object>();

		MessageWithSender mws = new MessageWithSender(sender, jd.deserialize(message));

		Message msg = _messageHandler.obtainMessage();
		msg.obj = mws;
		_messageHandler.sendMessage(msg);
	}
	
	
	
	@Override
	public void addOnLobbyUpdated(OnLobbyUpdatedListener listener) {
		_lobbyUpdated.add(listener);
	}

	@Override
	public void removeOnLobbyUpdated(OnLobbyUpdatedListener listener) {
		_lobbyUpdated.remove(listener);
	}

	@Override
	public void clearOnLobbyUpdated() {
		_lobbyUpdated.clear();
	}
	
	
	
	@Override
	public void addOnPlayerDisconnected(OnPlayerDisconnectedListener listener) {
		_playerDisconnected.add(listener);

	}
	
	@Override
	public void removeOnPlayerDisconnected(OnPlayerDisconnectedListener listener) {
		_playerDisconnected.remove(listener);
	}

	
	@Override
	public void clearOnPlayerDisconnected() {
		_playerDisconnected.clear();
	}
	
	
	
	@Override
	public void addOnPlayerConnected(OnPlayerConnectedListener listener) {
		_playerConnected.add(listener);
	}

	@Override
	public void removeOnPlayerConnected(OnPlayerConnectedListener listener) {
		_playerConnected.remove(listener);
	}

	@Override
	public void clearOnPlayerConnected() {
		_playerConnected.clear();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addMessageListener(Class clazz, OnMessageListener listener) {
		_messageListeners.add(new MessageListenerClass(listener, clazz));
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void removeMessageListeners(Class clazz) {
		for(MessageListenerClass listener : (CopyOnWriteArrayList<MessageListenerClass>)_messageListeners.clone()) {
			if(listener.getClazz().equals(clazz))
	        	_messageListeners.remove(listener);
		}
	}
}
