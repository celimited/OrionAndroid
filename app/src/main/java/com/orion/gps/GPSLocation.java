package com.orion.gps;

import android.content.Context;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSLocation {
	private static LocationManager locationManager;
	private static LocationListener locationListener;
	private static boolean isGPSEnabled;
	private static String ReturnedText = "";

	public static String GetLocation(Context context) {

		System.currentTimeMillis();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!isGPSEnabled) {
			// Toast.makeText(MainActivity.this, "GPS_Disabled", Toast.LENGTH_SHORT).show();
			ReturnedText = LocationConstant.GPS_DISABLED;
			return ReturnedText;
		}


		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {

			}

			@Override
			public void onProviderDisabled(String provider) {
			}


			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onStatusChanged(String provider, int status,
										Bundle extras) {
			}


		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		locationManager.addGpsStatusListener(mGPSStatusListener);

		return ReturnedText;
	}

	private static Listener mGPSStatusListener = new GpsStatus.Listener() {

		public void onGpsStatusChanged(int event) {
			switch (event) {
				case GpsStatus.GPS_EVENT_STARTED:

					ReturnedText = LocationConstant.GPS_SEARCHING;
					return;
				//  break;
				case GpsStatus.GPS_EVENT_STOPPED:

					ReturnedText = LocationConstant.GPS_STOPPED;
					return;
				//break;
				case GpsStatus.GPS_EVENT_FIRST_FIX:

					Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

					//Location location=loc;
					if (location != null) {
						ReturnedText = LocationConstant.GPS_FIXED + "," + location.getLatitude() + ", " + location.getLongitude()
						//+ " Accuracy = "+ location.getAccuracy()+ " Time= "+ calculateTimeDifference(location.getTime()) + " Provider = "+ location.getProvider()
						;

						locationManager.removeUpdates(locationListener);
						//locationManager.removeGpsStatusListener(mGPSStatusListener);


						return;
					} else {
						ReturnedText = "Not found";
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
	/*     			     	
	     			        Criteria criteria = new Criteria();
	     			        criteria.setCostAllowed(false);
	     			        criteria.setAccuracy(Criteria.ACCURACY_FINE);
	     			        String providerName = locationManager.getBestProvider(criteria, true);
	     			        locationManager.requestLocationUpdates(providerName, 400, 1, locationListener); */

						return;

					}

				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

					break;
			}

		}
	};
}