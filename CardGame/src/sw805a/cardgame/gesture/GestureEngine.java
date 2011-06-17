package sw805a.cardgame.gesture;

import java.util.ArrayList;

import sw805a.cardgame.ApplicationController;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GestureEngine implements SensorEventListener, IGestureEngine {
	public static int GESTURE_THROW_CARD = 0;
	
	private ArrayList<OnGestureListener> _listeners;
	private final SensorManager _sensorManager;
    private final Sensor _accelerometer;
    private static GestureEngine _instance = new GestureEngine();
    
    private int _minInterval = 1000;
    private long _lastGestureTime = System.currentTimeMillis();
    
    
    public static IGestureEngine getInstance(){
    	return _instance;
    }
    
    
    private GestureEngine() {
    	_listeners = new ArrayList<OnGestureListener>();
        _sensorManager = (SensorManager)ApplicationController.getContext().getSystemService(Context.SENSOR_SERVICE);
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        onResume();
    }
	
    public void onResume() {
        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    
    public void onPause() {
        _sensorManager.unregisterListener(this);
    }
	
	public void onSensorChanged(SensorEvent se) {
    	// 2 skærm/bagside
    	// 1 knap/stik
    	// 0 volume siden

		if(Math.abs(se.values[0]) > 13 && se.values[2] > 7){
			
			if(System.currentTimeMillis() > _lastGestureTime + _minInterval){
				notifyListeners("flick");
				_lastGestureTime = System.currentTimeMillis();
			}
			
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}


	@Override
	public void addGestureListener(OnGestureListener listener) {
		_listeners.add(listener);
		
	}


	@Override
	public void removeGestureListener(OnGestureListener listener) {
		_listeners.remove(listener);
		
	}
	
	private void notifyListeners(String id){
		for (OnGestureListener listener : _listeners) {
			listener.onGesture(id);
		}
	}


	@Override
	public void clearGestureListeners() {
		_listeners.clear();
	}

}