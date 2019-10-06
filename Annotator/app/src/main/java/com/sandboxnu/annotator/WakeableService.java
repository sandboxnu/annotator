package com.sandboxnu.annotator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WakeableService extends Service
{
    AlarmReceiver alarm = new AlarmReceiver();
    public void onCreate()
    {
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
        super.onDestroy();
        alarm.cancelAlarm(this);
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}