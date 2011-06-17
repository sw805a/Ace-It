package sw805a.cardgame.domain;

import java.util.Observable;

public class ReadyForSync extends Observable {
	private static ReadyForSync _instance = new ReadyForSync();
	public static ReadyForSync getInstance() {
		return _instance;
	}
	private ReadyForSync(){}
	
	private int _readyCount = 0;
	public void nextReady() {
		_readyCount++;
		setChanged();
		notifyObservers();
	}
	public void reset() {
		_readyCount = 0;
	}
	public int getReadyCount() {
		return _readyCount;
	}
}
