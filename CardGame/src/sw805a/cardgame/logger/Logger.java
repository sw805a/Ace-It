package sw805a.cardgame.logger;

public class Logger {
	public static void Log(String msg) {
		Log("CardGame",  msg);
	}
	public static void Log(String tag, String msg){
		android.util.Log.d(tag,msg);		
	}
}
