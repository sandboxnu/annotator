package com.sandboxnu.annotator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.sandboxnu.annotator.activitylogger.ActivityLogger;
import com.sandboxnu.annotator.activitylogger.ActivityLoggerImpl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main class for the Annotator application.
 */
public class MainActivity extends AppCompatActivity {

    private Context context;
    public static List<String> dataSet = new ArrayList<String>(Arrays.asList("walking", "talking",
            "eating", "running"));

    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshRecordingsView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerReceiver(refreshReceiver, new IntentFilter("refresh-view"));
        this.context = getApplicationContext();
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
        final ActivityLogger mainLogger = new ActivityLoggerImpl((SensorManager) getSystemService(SENSOR_SERVICE), context);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("restart", "activity");
        final Intent repeatingServiceIntent = new Intent(MainActivity.this, RepeatingService.class);
        ToggleButton toggle = findViewById(R.id.fab);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mainLogger.startLogFile();
                    startService(repeatingServiceIntent);
                    Log.d("Alarm", "Started");
                } else {
                    stopService(repeatingServiceIntent);
                    mainLogger.saveLogFile();
                    refreshRecordingsView();
                    Log.d("Alarm", "Stopped");
                }
            }
        });

        refreshRecordingsView();

        final MainActivity ref = this;



            // add the aws mobile client!
        //AWSMobileClient.getInstance().initialize(this).execute();

        // create the upload button, and upload files to s3 when it is clicked
        Button upload = findViewById(R.id.upload);

        // add a listener to the upload button to upload files
        upload.setOnClickListener(new View.OnClickListener() {
            // uploader
            RecordingUploader uploader = new RecordingUploader(ref);
            // onclick action to upload all of the local files
            public void onClick(View v) {
                //this.uploader.uploadAll();
                this.uploader.removeAll();
                refreshRecordingsView();
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

    public void refreshRecordingsView() {
        final File recordingsFolder = this.getFilesDir();
        File[] recordings = recordingsFolder.listFiles();
        String[] fileNames = new String[recordings.length];

        for(int i=0; i<recordings.length; i++) {
            fileNames[i] = recordings[i].getName();
        }
        ListView recordingsView = findViewById(R.id.recordings);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, fileNames);
        recordingsView.setAdapter(arrayAdapter);
    }
}

