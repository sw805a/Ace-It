package sw805a.cardgame.comm;

import flexjson.JSON;

public class Client {

	protected String _name;
	protected String _address;
	
	public Client(){}
	
	public Client(String address, String name){
		_address = address;
		_name = name;
	}
	
	@JSON
	public String getAddress() {
		return _address;
	}

	public void setAddress(String address) {
		_address = address;
	}
	
	@JSON
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}

}
