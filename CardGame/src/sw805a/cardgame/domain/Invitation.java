package sw805a.cardgame.domain;

import java.util.ArrayList;

import flexjson.JSON;

import sw805a.cardgame.comm.Client;

public class Invitation {
	private ArrayList<Client> _clients = new ArrayList<Client>();
	
	@JSON
	public ArrayList<Client> getClients() {
		return _clients;
	}

	public void setPlayers(ArrayList<Client> clients) {
		_clients = clients;
	}
	
	public Invitation(ArrayList<Client> clients) {
		_clients = clients;
	}
	
	public Invitation(){}

}
