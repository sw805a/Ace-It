package sw805a.cardgame.ui.decorators;

public class RGBColor {
	private int _r,_g,_b;
	public RGBColor(int r,int g, int b) {
		_r = r;
		_g = g;
		_b = b;
	}
	public int getRed() {
		return _r;
	}
	public int getGreen() {
		return _g;
	}
	public int getBlue() {
		return _b;
	}
}
