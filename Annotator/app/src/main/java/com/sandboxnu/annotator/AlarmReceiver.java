package com.sandboxnu.annotator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{
    static long INTERVAL = 5000;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("alarm", "wake up!");
        int tries = 0;
        while(tries < 5) {
            try {
                generateTone();
                break;
            } catch(RuntimeException e) {
                tries ++;
            }
        }
        Toast.makeText(context, "Asking user for voice input...", Toast.LENGTH_LONG).show();

        Intent intent1 = new Intent(context, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent1, 0);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, pendingIntent);
    }

    public void setAlarm(Context context)
    {
        Toast.makeText(context, "Recording", Toast.LENGTH_LONG).show();
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
    }

    public void cancelAlarm(Context context)
    {
        Toast.makeText(context, "Pausing", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void generateTone() {
        ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        beep.startTone(ToneGenerator.TONE_CDMA_PIP,200);
        beep.release();
    }
}