package com.example.myfirstapp;

import java.text.DecimalFormat;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private LocationListener li;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toast.makeText(getApplicationContext(), "Welcome to MySpeed v1.0.1!",
				Toast.LENGTH_SHORT).show();
		
		LocationManager locationManager = 
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    
	    if(gpsEnabled) 
	    {
            li = new speed();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, li);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_search:
	            //openSearch();
	            return true;
	        case R.id.action_settings:
	            //openSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public class speed implements LocationListener{
		private Location previousLocation;
		
        @Override
        public void onLocationChanged(Location currentLocation) {
        	// Float thespeed = loc.getSpeed() * MainActivity.METERS_PER_SECOND_TO_MILES_PER_HOUR;
            double speedMph = 0;
        	
    		if(previousLocation == null)
    		{
    			previousLocation = currentLocation;
    		}
    		else
    		{
    			speedMph = LocationUtils.speed(previousLocation, currentLocation);   			
    			previousLocation = currentLocation;
    		}
            
    		double latitude = currentLocation.getLatitude();
    		double longitude = currentLocation.getLongitude();
    		
    		TextView txtLatitude = (TextView) findViewById(R.id.text_latitude);
    		txtLatitude.setText(Double.toString(latitude));
    		
    		TextView txtLongitude = (TextView) findViewById(R.id.text_longitude);
    		txtLongitude.setText(Double.toString(longitude));
    		
    		DecimalFormat df = new DecimalFormat("#.##");
    		String speedMphFormatted = df.format(speedMph);
    		
    		TextView txtSpeed = (TextView) findViewById(R.id.text_speed);
    		txtSpeed.setText(speedMphFormatted);
        }
        
        @Override
        public void onProviderDisabled(String arg0) {}
        
        @Override
        public void onProviderEnabled(String arg0) {}
        
        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    }
	
	public static class LocationUtils
	{
		public final static float METERS_PER_SECOND_TO_MILES_PER_HOUR = (float) 2.23694;
		
		public static double distance(Location one, Location two) {
            int R = 6371000;        
            Double dLat = toRad(two.getLatitude() - one.getLatitude());
            Double dLon = toRad(two.getLongitude() - one.getLongitude());
            Double lat1 = toRad(one.getLatitude());
            Double lat2 = toRad(two.getLatitude());         
            Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);        
            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));        
            Double d = R * c;
            
            return d;
        }
		
		public static double speed(Location one, Location two)
		{
			double distance = LocationUtils.distance(one, two);
			Long diffns = two.getElapsedRealtimeNanos() - one.getElapsedRealtimeNanos();
			double seconds = diffns.doubleValue() / Math.pow(10, 9);
			
			double speedMph = distance / seconds;
			speedMph = speedMph * LocationUtils.METERS_PER_SECOND_TO_MILES_PER_HOUR;
			
			return speedMph;
		}
		
		private static double toRad(Double d) {
            return d * Math.PI / 180;
        }
	}
}
