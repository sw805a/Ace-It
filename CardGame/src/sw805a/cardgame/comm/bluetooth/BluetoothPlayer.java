package sw805a.cardgame.comm.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import sw805a.cardgame.comm.Client;

public class BluetoothPlayer extends Client {

	private BluetoothDevice _device;
	private BluetoothSocket _socket;
	private int _id;
	
	public BluetoothPlayer(BluetoothDevice device) {
		super(device.getAddress(), device.getName());
		_device = device;
	}

	public BluetoothDevice getDevice() {
		return _device;
	}
	
	public void setSocket(BluetoothSocket socket){
		_socket = socket;
	}
	
	public BluetoothSocket getSocket(){
		return _socket;
	}
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}
}
