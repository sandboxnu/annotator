package com.sandboxnu.annotator;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.Voice;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class VoiceRecognitionListener implements RecognitionListener {
    private List<String> data = new ArrayList();
    private RepeatingService.ListenForVoice func;

    /**
     * A constructor that takes in a function object ListenForVoice that will listen for voice
     * when applied
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
        for (int i = 0; i < data.size(); i++)
        {
            str += data.get(i);
        }
        // filter the str
        String result = this.speechResult(this.data);
        Log.d("VoiceRecognition", "result: " + str);
        Log.d("VoiceRecognition", "filter result: " + result);


    }

    /**
     * given a list of strings, returns a single string that exist
     * in our pre-existing list of inputs;
     * returns the first one that match (for now)
     * @param array
     */
    private String speechResult(List<String> array) {
        // if string not found, call listen for voice again
        while(!hasValidInput(array)) {
            func.apply();
        }
        // return first match
        for(String s: array) {
            if(MainActivity.dataSet.contains(s)) {
                return s;
            }
        }
        return "";
    }

    private boolean hasValidInput(List<String> array) {
        for(String s: array) {
            if(MainActivity.dataSet.contains(s)) {
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
