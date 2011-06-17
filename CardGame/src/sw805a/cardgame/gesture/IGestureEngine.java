package sw805a.cardgame.gesture;

public interface IGestureEngine {
	void addGestureListener(OnGestureListener listener);
	void removeGestureListener(OnGestureListener listener);
	void clearGestureListeners();
}
