package sw805a.cardgame.comm.listeners;

import java.util.*;

import sw805a.cardgame.comm.Client;

public interface OnLobbyUpdatedListener {
	public void onLobbyUpdated(List<Client> players);
}
