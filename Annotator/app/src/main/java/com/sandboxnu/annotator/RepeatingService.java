package com.sandboxnu.annotator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

public class RepeatingService extends Service
{
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    Handler mHandler;
    Runnable runnable;
    SpeechRecognizer speechRecognizer;
    static int delay = 10000;
    public void onCreate()
    {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new VoiceRecognitionListener(new ListenForVoice()));
        screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "annotator:keepRunning");
        screenLock.acquire();
        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                generateTone();
                Log.d("VoiceRecognition", "Listening!");
                listenForVoice();

                mHandler.postDelayed(this, delay);
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
        speechRecognizer.destroy();
        wakeLock.release();
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
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.sandboxnu.annotator");
        speechRecognizer.startListening(intent);
    }

    class ListenForVoice {
        void apply() {
            RepeatingService.this.listenForVoice();
        }
    }
}
