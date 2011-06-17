package sw805a.cardgame.comm.internet.messages;

import flexjson.JSON;


public class LobbyUpdate {
	public enum Direction {
		JOIN,
		LEAVE
	}
	private Direction mDirection;
	private String mAddress;
	private String mName;
	
	public LobbyUpdate() {}
	
	public LobbyUpdate(Direction dir, String addr, String name) {
		setDirection(dir);
		setAddress(addr);
		setName(name);
	}
	
	public void setDirection(Direction mDirection) {
		this.mDirection = mDirection;
	}
	@JSON
	public Direction getDirection() {
		return mDirection;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}
	@JSON
	public String getAddress() {
		return mAddress;
	}

	public void setName(String mName) {
		this.mName = mName;
	}
	@JSON
	public String getName() {
		return mName;
	}


}
