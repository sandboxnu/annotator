package com.sandboxnu.annotator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;
import android.speech.RecognizerIntent;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app:Wake");
       // final Intent alarmIntent = new Intent(MainActivity.this, WakeableService.class);
        final Intent alarmIntent = new Intent(MainActivity.this, RepeatingService.class);
        ToggleButton toggle = findViewById(R.id.fab);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(alarmIntent);
                    Log.d("Alarm", "Started");
                    wl.acquire();
                } else {
                    stopService(alarmIntent);
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
