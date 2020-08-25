/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.erezbiox1.paytimer.EditShift.EditShiftActivity;
import com.erezbiox1.paytimer.ListShifts.ListShiftsActivity;
import com.erezbiox1.paytimer.Settings.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.erezbiox1.paytimer.EditShift.EditShiftActivity.*;

public class MainActivity extends AppCompatActivity implements TimerService.Callbacks{

    // Constants used in various intents.
    public static final String START_TIME_PREF = BuildConfig.APPLICATION_ID + ".prefs.START_TIME_PREF";
    public static final String START_TIME_EXTRA_SERVICE = BuildConfig.APPLICATION_ID + ".extra.service.START_TIME_PREF";

    // Ui elements
    private Button startButton, shiftsButton;
    private ImageView anchor, startSymbol;
    private Animation rotatingAnimation;
    private TextView timerText;
    private AnimatedVectorDrawable startingAnimation;

    // Non-ui variables
    private boolean isRunning = false;
    private Intent serviceIntent;
    private TimerService timerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the toolbar as the action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the serviceIntent that will be used to start the service
        serviceIntent = new Intent(MainActivity.this, TimerService.class);

        // Get the UI elements.
        timerText = findViewById(R.id.timer);
        anchor = findViewById(R.id.ic_anchor);
        startButton = findViewById(R.id.start_shift);
        startSymbol = findViewById(R.id.start_symbol);
        shiftsButton = findViewById(R.id.list_shifts_btn);

        // Get the animations
        rotatingAnimation = AnimationUtils.loadAnimation(this, R.anim.rotating);
        startingAnimation = (AnimatedVectorDrawable) startSymbol.getDrawable();

        // Set the "start shift button" functionality.
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = !isRunning;
                updateUI();
            }
        });

        // Set the "list shifts button" functionality.
        shiftsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listIntent = new Intent(MainActivity.this, ListShiftsActivity.class);
                startActivity(listIntent);
            }
        });

        // If there is a "Starting Time" saved in storage (storage=sharedPreference)
        // Then set the timer active and update the UI accordingly.
        if(getSharedPreferences("TimePref", 0).contains(START_TIME_PREF)){
            isRunning = true;
            updateUI();
        }
    }

    /**
     * Updates the UI according to the timer status.
     * ( If the timer is active and updateUI() is called then the animations will start,
     *   if the timer is inactive the animations will be cleared. )
     *   This function is also used to start and stop the textual timer.
     */
    private void updateUI(){
        if(isRunning){
            // If the timer is active, start the anchor rotating animation, the symbol
            // changing animation and change the action button to "stop now".
            anchor.startAnimation(rotatingAnimation);
            startingAnimation.start();
            startButton.setText(R.string.stop_now);

            // Start the timer
            startTimer();
        } else {
            // Stop and clear all animations from above.
            anchor.clearAnimation();
            startingAnimation.reset();
            startButton.setText(R.string.start_now);
            timerText.setText("");

            // Stop the timer
            stopTimer();
        }
    }

    /**
     * Starts the timer
     */
    private void startTimer(){
        // Get the starting time
        SharedPreferences pref = getSharedPreferences("TimePref", 0);
        long currentTime = pref.getLong(START_TIME_PREF, System.currentTimeMillis());

        // Save the starting time
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(START_TIME_PREF, currentTime);
        editor.apply();

        // Add the starting time to the intent passed to the service
        serviceIntent.putExtra(START_TIME_EXTRA_SERVICE, currentTime);

        // Start ( and bind ) the service that will update the text
        startService(serviceIntent);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Stops the timer
     */
    private void stopTimer(){
        // Stops the timer service ( stop the counter, unbound then stop the service )
        timerService.stopCounter();
        unbindService(connection);
        stopService(serviceIntent);

        // Get the start and end times
        long startingTime = getSharedPreferences("TimePref", 0).getLong(START_TIME_PREF, System.currentTimeMillis());
        long endingTime = System.currentTimeMillis();

        // Clear the saved starting time.
        SharedPreferences pref = getSharedPreferences("TimePref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        // Add the shift
        Intent intent = new Intent(MainActivity.this, EditShiftActivity.class);
        intent.putExtra(START_TIME_EXTRA, startingTime);
        intent.putExtra(END_TIME_EXTRA, endingTime);
        startActivity(intent);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            // Cast the binder to the inner custom binder class specified in TimerService
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getServiceInstance();

            // Register this (calling) activity as a client for future callbacks
            timerService.registerClient(MainActivity.this);

            // Start the counter
            timerService.startCounter();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { }
    };

    /**
     * Sets the timer text
     * @param millis Current shift duration
     */
    @Override
    public void updateClient(long millis) {
        // Hours:Minutes:Seconds format for the timer text. ( i.e. 0:00:00 )
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // UTC+0 for the time zone as System.currentTimeMillis() returns it in UTC+0 ( epoch time )
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String text = format.format(new Date(millis));

        // Set the timer text.
        timerText.setText(text);
    }

    /**
     * Inflates the options menu
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * On option selected listener, TODO: to be implemented in the future
     * @param item
     * @return was the item clicked handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
