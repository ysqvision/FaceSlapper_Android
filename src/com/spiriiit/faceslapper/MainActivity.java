package com.spiriiit.faceslapper;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
	private SensorManager senSensorManager;
	private Sensor senAccelerometer;
	private MediaPlayer mediaPlayer;
	private boolean previousPositive;
	private boolean readyToSlap = true;
	private long previousTime = 0;
	//private static final int SHAKE_THRESHOLD = 600;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* Initialize private variables. */
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.faceslap);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
	    if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
	    	float z = sensorEvent.values[2];
	    	if ((z > 0) != previousPositive) {
	    		readyToSlap = true;
	    	}
	    	if (Math.abs(z) > 10) {
	    		long currentTime = System.currentTimeMillis();		
	    		if (readyToSlap && (currentTime - previousTime) > 200) {
		    		if (mediaPlayer.isPlaying()) {
		    			mediaPlayer.pause();
		    			mediaPlayer.seekTo(0);
		    		}
		    		mediaPlayer.start();
		    		System.out.println("slapped with z = " + z);
		    		previousPositive = (z > 0);
		    		previousTime = currentTime;
	    		}
	    		readyToSlap = false;
	    	}
	    }	
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    senSensorManager.unregisterListener(this);
	    mediaPlayer.release();
	    mediaPlayer = null;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.faceslap);
	    senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
}
