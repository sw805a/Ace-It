package sw805a.cardgame.comm.internet.server;

public class CardGameServer {
	public static int LobbyPort = 8002;
	
	public static void main(String[] args) {
		try {
			LobbyAcceptThread lobby = new LobbyAcceptThread();
			lobby.start();
			System.out.println("Running server on port " + LobbyPort);
			do {
				int a = System.in.read();
				if (a == 'q') {
					break;
				}
				if (a == 'r') {
					lobby.reset();
					System.out.println("Reset!");
				}
			} while (true);
			lobby.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}	
}
