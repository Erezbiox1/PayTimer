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

    private Callbacks activity;
    private long startTime = 0;
    private long millis = 0;
    private final IBinder binder = new LocalBinder();
    private Handler handler = new Handler();

    private Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            activity.updateClient(millis);
            handler.postDelayed(this, 1000);
        }
    };

    public TimerService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = intent.getLongExtra(MainActivity.START_TIME_EXTRA_SERVICE, System.currentTimeMillis());
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        public TimerService getServiceInstance(){
            return TimerService.this;
        }
    }

    public void registerClient(Activity activity){
        this.activity = (Callbacks) activity;
    }

    public void startCounter(){
        handler.postDelayed(serviceRunnable, 0);
    }

    public void stopCounter(){
        handler.removeCallbacks(serviceRunnable);
    }

    interface Callbacks {
        public void updateClient(long data);
    }
}
