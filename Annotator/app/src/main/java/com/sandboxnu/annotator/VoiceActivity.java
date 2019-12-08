package com.sandboxnu.annotator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceActivity extends AppCompatActivity {

    private TextView voiceInput;
    private TextView speakButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public static String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askSpeechInput();

    }

    // Showing google speech input dialog

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // Receiving speech input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (String str: result){
                        // str: voice-to-text data
                       // Log.d("Voice", str);
                        VoiceActivity.message = str;
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("message", str);
                        startActivity(intent);

                    }
                }
                break;
            }

        }
    }
}