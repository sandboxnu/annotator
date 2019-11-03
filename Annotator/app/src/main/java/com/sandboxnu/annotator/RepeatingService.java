package com.sandboxnu.annotator;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class RepeatingService extends Service
{
    PowerManager.WakeLock screenLock;
    Handler mHandler;
    Runnable runnable;
    public void onCreate()
    {
        screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "annotator:keepRunning");
        screenLock.acquire();
        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                generateTone();
                listenForVoice();

                Log.d("RepeatingService", "running!");
                mHandler.postDelayed(this, 10000);
            }
        };

        mHandler.post(runnable);
        super.onCreate();
    }


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        screenLock.release();
        mHandler.removeCallbacks(runnable);
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void generateTone() {
        ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        beep.startTone(ToneGenerator.TONE_CDMA_PIP,200);
        beep.release();
    }

    private void listenForVoice() {
        Intent dialogIntent = new Intent(this, VoiceActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }
}