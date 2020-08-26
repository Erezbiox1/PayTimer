/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Reminders;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationHandler implements LocationListener {
    public static final int LOCATION_PERM_RESULT = 440;

    private LocationManager locationManager;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencingIntent;
    private SharedPreferences preferences;
    private Activity activity;

    public LocationHandler(Activity activity, SharedPreferences preferences) {
        this.activity = activity;
        this.preferences = preferences;
        this.geofencingClient = LocationServices.getGeofencingClient(activity);

        //addGeofence();
    }

    public void setGeofence(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            String[] perms = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(activity, perms, 1);
            return;
        }

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);

        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if(locationGps != null)
            onLocationChanged(locationGps);
        else if(locationNetwork != null)
            onLocationChanged(locationNetwork);
        else if(locationPassive != null)
            onLocationChanged(locationPassive);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        preferences
                .edit()
                .putFloat("location_reminder_lat", (float) lat)
                .putFloat("location_reminder_lon", (float) lon)
                .apply();

        locationManager.removeUpdates(this);

        addGeofence();

    }

    private void addGeofence(){
        if(preferences.contains("location_reminder_lat")
                && preferences.contains("location_reminder_lon")
                && preferences.contains("location_notifications")
                && preferences.getBoolean("location_notifications", false)){

            double lat = preferences.getFloat("location_reminder_lat", -1);
            double lon = preferences.getFloat("location_reminder_lon", -1);

            GeofencingRequest request = new GeofencingRequest
                    .Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(new Geofence
                            .Builder()
                            .setRequestId("main")
                            .setExpirationDuration((long) 1000 * 60 * 60 * 24 * 365 * 10)
                            .setCircularRegion(lat, lon, 250) // 50 meters radius.
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build()
                    ).build();

            if(geofencingIntent == null)
                geofencingIntent = PendingIntent
                        .getBroadcast(
                                activity, 0,
                                new Intent(activity, GeofenceReceiver.class),
                                PendingIntent.FLAG_UPDATE_CURRENT);

            geofencingClient
                    .addGeofences(request, geofencingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(activity, "Location marked successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
            });
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }
}
