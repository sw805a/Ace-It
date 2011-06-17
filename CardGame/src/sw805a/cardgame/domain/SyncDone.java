package sw805a.cardgame.domain;

import java.util.Observable;

public class SyncDone extends Observable {
	private static SyncDone _instance = new SyncDone();
	public static SyncDone getInstance() {
		return _instance;
	}
	
	private SyncDone() {}
	
	public void synced(){
		setChanged();
		notifyObservers();
	}
}
