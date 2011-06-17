package sw805a.cardgame.comm.internet;

import java.net.Socket;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import sw805a.cardgame.comm.Client;

public class InternetPlayer extends Client {

	private Socket _socket;
	private int _id;
	
	public InternetPlayer(String address, String name) {
		super(address,name);
	}

	public void setSocket(Socket socket) {
		_socket = socket;
	}
	
	public Socket getSocket(){
		return _socket;
	}
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}
}
