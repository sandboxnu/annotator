package com.sandboxnu.annotator;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.speech.RecognizerIntent;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)
            return;
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Record audio is required", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 527);
            }
        }

        Log.i("messageReceived",  VoiceActivity.message);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("restart", "activity");

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app:Wake");
        final Intent repeatingServiceIntent = new Intent(MainActivity.this, RepeatingService.class);



        ToggleButton toggle = findViewById(R.id.fab);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    startService(repeatingServiceIntent);
                    Log.d("Alarm", "Started");
                    wl.acquire();
                } else {
                    stopService(repeatingServiceIntent);
                    Log.d("Alarm", "Stopped");
                    wl.release();
                }
            }
        });

        File recordingsFolder = this.getFilesDir();
        File[] recordings = recordingsFolder.listFiles();
        String[] fileNames = new String[recordings.length];
        for(int i=0; i<recordings.length; i++) {
            fileNames[i] = recordings[i].getName();
        }
        ListView recordingsView = findViewById(R.id.recordings);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, fileNames);
        recordingsView.setAdapter(arrayAdapter);

//        Intent intent = getIntent();
//        String str = intent.getStringExtra("message");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
