package sw805a.cardgame.comm.listeners;

import sw805a.cardgame.comm.Client;

public interface OnMessageListener {
	public void OnMessage(Client sender, Object message);
}
