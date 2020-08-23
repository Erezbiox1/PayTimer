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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.erezbiox1.paytimer.EditShift.EditShiftActivity.*;

public class MainActivity extends AppCompatActivity implements TimerService.Callbacks{

    public static final String START_TIME_PREF = BuildConfig.APPLICATION_ID + ".prefs.START_TIME_PREF";
    public static final String START_TIME_EXTRA_SERVICE = BuildConfig.APPLICATION_ID + ".extra.service.START_TIME_PREF";

    private Button startButton;
    private ImageView anchor, startSymbol;
    private Animation rotatingAnimation;
    private TextView timerText;
    private AnimatedVectorDrawable startingAnimation;

    private boolean isRunning = false;
    private Intent serviceIntent;
    private TimerService timerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serviceIntent = new Intent(MainActivity.this, TimerService.class);
        timerText = findViewById(R.id.timer);

        anchor = findViewById(R.id.ic_anchor);
        startButton = findViewById(R.id.start_shift);
        startSymbol = findViewById(R.id.start_symbol);

        rotatingAnimation = AnimationUtils.loadAnimation(this, R.anim.rotating);
        startingAnimation = (AnimatedVectorDrawable) startSymbol.getDrawable();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = !isRunning;
                updateUI();
            }
        });

        if(getSharedPreferences("TimePref", 0).contains(START_TIME_PREF)){
            isRunning = true;
            updateUI();
        }
    }

    private void updateUI(){
        if(isRunning){
            anchor.startAnimation(rotatingAnimation);
            startingAnimation.start();
            startButton.setText(R.string.stop_now);

            startTimer();
        } else {
            anchor.clearAnimation();
            startingAnimation.reset();
            startButton.setText(R.string.start_now);
            timerText.setText("");

            stopTimer();
        }
    }

    private void startTimer(){
        // Get the starting time, and save it.
        SharedPreferences pref = getSharedPreferences("TimePref", 0);
        long currentTime = pref.getLong(START_TIME_PREF, System.currentTimeMillis());

        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(START_TIME_PREF, currentTime);
        editor.apply();

        // Add the starting time to the intent
        serviceIntent.putExtra(START_TIME_EXTRA_SERVICE, currentTime);

        // Start the service that will update the text
        startService(serviceIntent);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private void stopTimer(){
        // Stops the timer
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
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getServiceInstance();
            timerService.registerClient(MainActivity.this);
            timerService.startCounter();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //timerService.stopCounter();
        }
    };


    @Override
    public void updateClient(long millis) {
        SimpleDateFormat format;
        if(millis / 1000 < 3600)
            format = new SimpleDateFormat("mm:ss", Locale.getDefault());
        else
            format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String text = format.format(new Date(millis));
        timerText.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
