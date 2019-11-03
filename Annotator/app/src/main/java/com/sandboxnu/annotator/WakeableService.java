package com.sandboxnu.annotator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class WakeableService extends Service
{
    AlarmReceiver alarm = new AlarmReceiver();
    PowerManager.WakeLock screenLock;
    public void onCreate()
    {
        screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "annotator:keepRunning");
        screenLock.acquire();

        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG);
        super.onCreate();
    }


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("Alarm", "wakable service");
        alarm.setAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        screenLock.release();
        super.onDestroy();
        alarm.cancelAlarm(this);
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}