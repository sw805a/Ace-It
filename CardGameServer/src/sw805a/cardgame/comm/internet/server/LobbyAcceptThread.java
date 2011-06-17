package sw805a.cardgame.comm.internet.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import sw805a.cardgame.comm.internet.messages.LobbyUpdate;

public class LobbyAcceptThread extends Thread {
	private ServerSocket mServerSocket;
	private boolean mRun = true;
	
	private String[] names =  {"Alice", "Bob", "Charlie", "David"};
	
	private ArrayList<LobbyReadThread> mReadThreads = new ArrayList<LobbyReadThread>();
	
	public LobbyAcceptThread() throws Exception {
		mServerSocket = new ServerSocket(CardGameServer.LobbyPort);
	}
	
	public void run() {
		while (mRun) {
			try {
				Socket socket = mServerSocket.accept();
				if (socket != null) {
					LobbyReadThread readThread = new LobbyReadThread(socket);
					readThread.start();
					String myname = names[mReadThreads.size()];
					System.out.println("Accepting a client: "+myname);
					int i = 0;
					for (LobbyReadThread thread : mReadThreads) {
						String hostip = socket.getInetAddress().toString();
						hostip = hostip.substring(hostip.indexOf('/')+1);
						String name = names[i];  
						thread.send(new LobbyUpdate(LobbyUpdate.Direction.JOIN, hostip, myname));
						hostip = thread.getSocket().getInetAddress().toString();
						hostip = hostip.substring(hostip.indexOf('/')+1);
						readThread.send(new LobbyUpdate(LobbyUpdate.Direction.JOIN,hostip, name));
						i++;
					}
					mReadThreads.add(readThread);
				}
			} catch (Exception e) {
				
			}
		}
	}
	public void close() {
		mRun = false;
		for (LobbyReadThread thread : mReadThreads) {
			thread.close();
		}
		
		try {
			mServerSocket.close();
		} catch (IOException e) {

		}
		
	}
	public class LobbyReadThread extends Thread  {
		private Socket mSocket;
		private InputStream mInStream;
		private OutputStream mOutStream;
		
		public Socket getSocket() {
			return mSocket;
		}
		
		public LobbyReadThread(Socket socket) throws Exception {
			mSocket = socket;
			mInStream = socket.getInputStream();
			mOutStream = socket.getOutputStream();
		}
		
		public void run() {
			StringBuilder msg;
			int input;
			while (true) {
	        	msg = new StringBuilder();
				try {
					do {
						input = mInStream.read();
						if (input == -1) {
							break;
						}
						msg.append((char)input);
					} while(input != '\0');
					
					if (input == -1) {
						break;
					}
					JSONDeserializer<Object> jd = new JSONDeserializer<Object>();

					Object object = jd.deserialize(msg.toString());
					
				} catch (IOException e) {
	            	
	                break;
	            }
	        }
			mReadThreads.remove(this);
			System.out.println("Client left");
		}
		public void close() {
			try {
				mSocket.close();
			} catch (IOException e) {
				
			}
		}
		
		public void send(Object object) {
			JSONSerializer s = new JSONSerializer();
			String strMsg = s.serialize(object);
			try {
				mOutStream.write(((strMsg + '\0').getBytes()));
			} catch (IOException e) {

			}
		}

	}
}