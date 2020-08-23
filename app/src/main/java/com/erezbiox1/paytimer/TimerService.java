package com.erezbiox1.paytimer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    // Activity to callback to
    private Callbacks activity;

    // Starting and current time in milliseconds.
    private long startTime = 0;
    private long millis = 0;

    // The binder used to allow the activity to register itself for callbacks
    private final IBinder binder = new LocalBinder();

    // Handler used to do actions on the UI thread ( Main process )
    private Handler handler = new Handler();

    // The service runnable, schedule itself every second,
    // calling the callback with the current shift duration
    private Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            activity.updateClient(millis);
            handler.postDelayed(this, 1000);
        }
    };

    // Empty constructor required by the Service class.
    public TimerService() { }

    /**
     * @param intent Starting intent
     * @return the binder that is used by the activity to register itself for callbacks
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Starting method for the service
     * @param intent used to get the starting time specified by the activity
     *               ( can be different from the current time due if the app
     *                 was killed )
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = intent.getLongExtra(MainActivity.START_TIME_EXTRA_SERVICE, System.currentTimeMillis());
        return START_NOT_STICKY;
    }

    /**
     * LocalBinder class, a custom binder that is passed to the activity
     * With the service reference.
     */
    public class LocalBinder extends Binder {
        /**
         * @return the timer service
         */
        public TimerService getServiceInstance(){
            return TimerService.this;
        }
    }

    /**
     * Registers the activity for callbacks
     * @param activity that will be registered for callbacks
     */
    public void registerClient(Activity activity){
        this.activity = (Callbacks) activity;
    }

    /**
     * Start the counter ( by running the runnable through the handler )
     */
    public void startCounter(){
        handler.postDelayed(serviceRunnable, 0);
    }

    /**
     * Stopping the counter ( by removing the runnable call )
     */
    public void stopCounter(){
        handler.removeCallbacks(serviceRunnable);
    }

    /**
     * Callbacks interface used to communicate with the activity
     */
    interface Callbacks {
        /**
         * a listener method that is implemented by the activity and
         * is called every second by the service runnable with the
         * current shift's duration.
         * @param data the current shifts duration
         */
        void updateClient(long data);
    }
}
