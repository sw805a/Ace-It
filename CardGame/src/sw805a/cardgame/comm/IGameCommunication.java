package sw805a.cardgame.comm;

import sw805a.cardgame.comm.listeners.*;

public interface IGameCommunication {
	
	void enterLobby();
	void leaveLobby();
	
	void setInviteeCount(int size);
	int getInviteeCount();
	int getInvitedCount();
	void invite(Client target) throws Exception;
	
	void disconnect();
	
	void addOnLobbyUpdated(OnLobbyUpdatedListener listener);
	void removeOnLobbyUpdated(OnLobbyUpdatedListener listener);
	void clearOnLobbyUpdated();
	
	void addOnPlayerDisconnected(OnPlayerDisconnectedListener listener);
	void removeOnPlayerDisconnected(OnPlayerDisconnectedListener listener);
	void clearOnPlayerDisconnected();

	void addOnPlayerConnected(OnPlayerConnectedListener listener);
	void removeOnPlayerConnected(OnPlayerConnectedListener listener);	
	void clearOnPlayerConnected();
	
	@SuppressWarnings("rawtypes")
	void addMessageListener(Class clazz, OnMessageListener listener);
	@SuppressWarnings("rawtypes")
	void removeMessageListeners(Class clazz);
	
	void sendMessage(Object msg);
	void sendMessage(Client target, Object msg);
	
	String getMyAddress();
	String getMyName();
	
}
