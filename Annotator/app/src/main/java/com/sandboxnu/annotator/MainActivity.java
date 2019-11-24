package com.sandboxnu.annotator;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.sandboxnu.annotator.activitylogger.ActivityLogger;
import com.sandboxnu.annotator.activitylogger.ActivityLoggerImpl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;


import java.io.File;

/**
 * The main class for the Annotator application.
 */
public class MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.context = getApplicationContext();

        ActivityLogger mainLogger = new ActivityLoggerImpl((SensorManager) getSystemService(SENSOR_SERVICE), context);
        mainLogger.startLogFile();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent alarmIntent = new Intent(MainActivity.this, WakeableService.class);
        ToggleButton toggle = findViewById(R.id.fab);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(alarmIntent);
                    Log.d("Alarm", "Started");
                } else {
                    stopService(alarmIntent);
                    Log.d("Alarm", "Stopped");
                }
            }
        });

        final File recordingsFolder = this.getFilesDir();
        File[] recordings = recordingsFolder.listFiles();
        String[] fileNames = new String[recordings.length];

        for(int i=0; i<recordings.length; i++) {
            fileNames[i] = recordings[i].getName();
        }

        final MainActivity ref = this;

        ListView recordingsView = findViewById(R.id.recordings);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, fileNames);
        recordingsView.setAdapter(arrayAdapter);

        // add the aws mobile client!
        AWSMobileClient.getInstance().initialize(this).execute();

        // create the upload button, and upload files to s3 when it is clicked
        Button upload = findViewById(R.id.upload);

        // add a listener to the upload button to upload files
        upload.setOnClickListener(new View.OnClickListener() {
            // uploader
            RecordingUploader uploader = new RecordingUploader(ref);
            // onclick action to upload all of the local files
            public void onClick(View v) {
                this.uploader.uploadAll();
            }
        });
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

