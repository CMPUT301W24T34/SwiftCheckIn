package com.example.swiftcheckin.attendee;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;


public class LocationReceiver {
    FirebaseAttendee fb;
    private static final int LOCATION_PERMISSION = 1001;
    private LocationManager locationManager;
    private LocationListener locationListener;
    // this file works on getting the location from the users device

    /**
     * constructs the locationreceiver object
     */
    public LocationReceiver(){
        fb = new FirebaseAttendee();
    }

    /**
     * Makes sure location permission is turned on then calls method to get the latitude and longitude
     * @param deviceId - device id of phone
     * @param context - context
     */
    public void getLocation(String deviceId, Context context) {
        fb.getLocation(deviceId, context, new FirebaseAttendee.IsLocationPermission() {
            @Override
            public void GetLocationCallback(boolean bool) {
                if (bool) {
                    saveLocation(deviceId, context);
                }
            }
        });
    }

    // Citation: OpenAI, 03-29-2024, ChatGPT, How to obtain location of user
    // output was to use a location manager and listener, gave me the onLocationChanged, onProviderDisabled, onProviderEnabled, onStatusChanged methods
    // also gave the checking of permissions

    /**
     * Gets the latitude and longitude coordinates of the users device
     * @param deviceId - device id of phone
     * @param context - context
     */
    public void saveLocation(String deviceId, Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                    // Citation: OpenAI, 03-29-2024, ChatGPT, How to ensure that the updates don't keep coming
                    // output was locationManager.removeUpdates(this);
                    // Remove updates after first one
                    locationManager.removeUpdates(this);
                    // Save location data to Firebase
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("latitude", String.valueOf(latitude));
                    data.put("longitude", String.valueOf(longitude));
                    fb.updateLocationInfo(deviceId, data);
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Log.d("Location", "Provider disabled: " + provider);
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    Log.d("Location", "Provider enabled: " + provider);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("Location", "Provider status changed: " + provider);
                }
            };

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);

    }

}
