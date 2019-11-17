package com.sandboxnu.annotator.activitylogger;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.sandboxnu.annotator.utils.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;


public class ActivityLoggerImpl implements ActivityLogger {
    private String logFileName;
    private ArrayList<ArrayList<Float>> logData;
    private SensorManager sensorManager;
    private SensorEventListener listener;
    private ArrayList<String> timestamps;
    private Context ctx;

    private void initializeLogs() {
        this.logData = new ArrayList<>(Arrays.asList(new ArrayList<Float>(), new ArrayList<Float>(), new ArrayList<Float>()));
        this.timestamps = new ArrayList<>();
        this.logFileName = null;
    }


    public ActivityLoggerImpl(SensorManager sensorManager, Context ctx) {
        this.initializeLogs();
        this.sensorManager = sensorManager;
        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                // Get timestamp
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());
                timestamps.add(nowAsISO);


                System.out.println(event.values);
                Sensor sensor = event.sensor;
                float[] values = event.values;
                logData.get(0).add(values[0]);
                logData.get(1).add(values[1]);
                logData.get(2).add(values[2]);
            }
        };

    }

    public String saveLogFile() {
        sensorManager.unregisterListener(this.listener);
        String fileName = this.logFileName;
        if (fileName == null) {
            fileName = StringUtils.getAlphaNumericString(12);
        }

        StringBuilder fileContents = new StringBuilder("TS,X,Y,Z\n");
        for (int i = 0; i < this.logData.get(0).size(); i++) {
            fileContents
                    .append(timestamps.get(i))
                    .append(",")
                    .append(logData.get(0).get(i))
                    .append(",")
                    .append(logData.get(1).get(i))
                    .append(",")
                    .append(logData.get(2).get(i))
                    .append('\n');
        }

        try {
            FileOutputStream outputStream = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContents.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public void nameLogFile(String name) {
        this.logFileName = name + ".csv";
    }

    public String getLogFileName() {
        return this.logFileName;
    }

    public void startLogFile() {
        sensorManager.registerListener(this.listener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
    }

    public FileInputStream getLogFileByName(String name) {
        try {
            return ctx.openFileInput(name);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}