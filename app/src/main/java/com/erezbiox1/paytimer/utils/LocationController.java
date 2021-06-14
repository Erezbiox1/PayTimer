/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.utils;

import android.Manifest;
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
import androidx.preference.PreferenceManager;

import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.background.receivers.GeofenceReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationController implements LocationListener {

    // Constants
    public static final int LOCATION_PERM_RESULT = 440;
    public static final String LOCATION_REMINDER_LON = "location_reminder_lon";
    public static final String LOCATION_REMINDER_LAT = "location_reminder_lat";

    // The location manager, geo-fencing client, broadcast intent, prefs and provided context
    private LocationManager locationManager;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencingIntent;
    private SharedPreferences preferences;
    private Context context;

    public LocationController(Context context) {
        // Save the provided context
        this.context = context;

        // Load the default prefs from the context
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Load the geofencing client from the location services.
        this.geofencingClient = LocationServices.getGeofencingClient(context);
    }

    /**
     * Save the current location to the prefs ( or more accurately, request the current location )
     * and then reconfigure the geofence to the provided coordinates.
     */
    public void setGeofence(){
        // Check if we have the necessary location permission
        if(!checkPermission(context))
            return;

        // Get the location manager service
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Request location updates from the GPS, NETWORK and PASSIVE providers.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);

        // Try getting the last known location ( this returned null in most of my testing )
        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        // If either of the locations was found, let the controller handle it like it
        // just a normal event from the requestLocationUpdates() listener
        if(locationGps != null)
            onLocationChanged(locationGps);
        else if(locationNetwork != null)
            onLocationChanged(locationNetwork);
        else if(locationPassive != null)
            onLocationChanged(locationPassive);
    }

    /**
     * Called when the location manager location updates listener returns a location
     * @param location the current user location
     */
    @Override
    public void onLocationChanged(Location location) {
        // Get the lat and lon coordinates of the user location
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        // Save the coordinates in storage ( shared prefs )
        preferences
                .edit()
                .putFloat(LOCATION_REMINDER_LAT, (float) lat)
                .putFloat(LOCATION_REMINDER_LON, (float) lon)
                .apply();

        // Unregister for updates from the listener ( we got the location, now we're not interested in further location updates
        locationManager.removeUpdates(this);

        // Add the geofence
        addGeofence();
    }

    /**
     * Adds the geofence defined by the stored coordinates in the shared prefs.
     */
    public void addGeofence(){
        addGeofence(false);
    }

    /**
     * Adds the geofence defined by the stored coordinates in the shared prefs.
     * @param silent should the method notify the user of a success ( or a failure )
     */
    public void addGeofence(final boolean silent){
        // Check if the following conditons apply:
        // we have the necessary location permission,
        // the stored prefs contains the user specified lat and lon coordinates,
        // the location notifications are enabled ( default to false )
        if(checkPermission(context)
                && preferences.contains(LOCATION_REMINDER_LAT)
                && preferences.contains(LOCATION_REMINDER_LON)
                && preferences.getBoolean("location_notifications", false)){

            // Get the lat and lon ( default to -1 but that is impossible
            // since we checked for their existence already
            double lat = preferences.getFloat(LOCATION_REMINDER_LAT, -1);
            double lon = preferences.getFloat(LOCATION_REMINDER_LON, -1);

            // Create a geofencing request
            GeofencingRequest request = new GeofencingRequest
                    .Builder()
                    // Set the initial trigger to be when you enter the geofence.
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(new Geofence
                            .Builder()
                            .setRequestId("main")
                            // Set no expiration date
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            // Set the coordinates and a 150 meters radius perimeter
                            .setCircularRegion(lat, lon, 150)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build()
                    ).build();

            // If there was no previous geofencing intent, create a new one
            if(geofencingIntent == null)
                // a broadcast intent that will alert the geofence receiver.
                geofencingIntent = PendingIntent
                        .getBroadcast(
                                context, 0,
                                new Intent(context, GeofenceReceiver.class),
                                PendingIntent.FLAG_UPDATE_CURRENT);

            // Clear previous geo fences.
            geofencingClient.removeGeofences(geofencingIntent);

            // Add the geofencing request and intent to the client.
            geofencingClient
                    .addGeofences(request, geofencingIntent)
                    .addOnSuccessListener(aVoid -> {
                        // If the method isn't silent, notify the user ( via toast ) that the location marking was successful!
                        if(!silent)
                            Toast.makeText(context, R.string.location_marked_success, Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        if(!silent)
                            // If the method isn't silent, notify the user ( via toast ) that the location marking was failed.
                            Toast.makeText(context, R.string.Geofence_not_available, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        }
    }

    // Empty methods for the LocationListener interface, none of this events interest us.

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }

    /**
     * Check if the user allowed the app to use location services. ( Granted location permission )
     * @param context provided context
     * @return weather or not the location permissions are granted
     */
    public static boolean checkPermission(Context context){

        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
