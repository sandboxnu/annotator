package com.sandboxnu.annotator;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.Voice;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VoiceRecognitionListener implements RecognitionListener {
    private List<String> data = new ArrayList();
    private RepeatingService.ListenForVoice func;

    /**
     * A constructor that takes in a function object ListenForVoice that will listen for voice
     * when applied
     *
     * @param func - the function object
     */

    public VoiceRecognitionListener(RepeatingService.ListenForVoice func) {
        this.func = func;
    }


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("VoiceRecognition", "ready for speech");
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        Log.d("VoiceRecognition", "error code: " + error);
    }

    @Override
    public void onResults(Bundle results) {
        String str = new String();
        this.data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++) {
            str += data.get(i);
        }
        // gets the confidence scores
        float[] confidence = results.getFloatArray(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
        // filter the str
        String result = this.speechResult(this.data, confidence);
        Log.d("VoiceRecognition", "result: " + str);
        Log.d("VoiceRecognition", "filter result: " + result);


    }

    /**
     * given a list of strings, returns a single string that exist
     * in our pre-existing list of inputs;
     * returns the first one that match (for now)
     *
     * @param array
     */
    private String speechResult(List<String> array, float[] confidence) {
        // if string not found, call listen for voice again
        while (!hasValidInput(array)) {
            func.apply();
        }

        // filters the results to just the ones in the dictionary
        // of the matches, we return the result that yields the highest confidence
        // level
        double maxConfidence = 0.0;
        int maxIndex = 0;
        for (int i = 0; i < array.size(); i++) {
            // if in dictionary
            if (MainActivity.dataSet.contains(array.get(i))) {
                // if greater confidence
                if (confidence[i] > maxConfidence) {
                    maxConfidence = confidence[i];
                    maxIndex = i;
                }

            }
        }

        save(array.get(maxIndex));
        return array.get(maxIndex) + "with the confidence level of: " + maxConfidence;
    }

    // saves the label to file
    public static void save(String label) {
        // file name is time stamp, content = label
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());

        String home = System.getProperty("user.home");
        File file = new File(home+"/Downloads/" + date + ".txt");

        if (file.exists() && file.isFile()) {
            System.out.println("This file already exists.");
            return;
        } else {
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(file);
            } catch (FileNotFoundException fne) {
                System.out.println("file not found or could not be created!");
            }

            printWriter.append(label);
            printWriter.close();
        }
        System.out.println("Saved!");
    }

    private boolean hasValidInput(List<String> array) {
        for (String s : array) {
            if (MainActivity.dataSet.contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
