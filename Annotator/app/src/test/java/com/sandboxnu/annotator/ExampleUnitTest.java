package com.sandboxnu.annotator;

import android.speech.tts.Voice;

import com.jcraft.jsch.IO;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSaveTestCorrect1() throws IOException {
        VoiceRecognitionListener.save("hello");
        String home = System.getProperty("user.home");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        assertEquals("hello\n",
                readFile(home+"/Downloads/" + date + ".txt"));

    }

    @Test
    public void testSaveTestCorrect2() throws IOException {
        VoiceRecognitionListener.save("GOODBYE");
        String home = System.getProperty("user.home");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        assertEquals("GOODBYE\n",
                readFile(home+"/Downloads/" + date + ".txt"));

    }

    @Test
    public void testSaveCantSaveToSameFile() throws IOException {
        VoiceRecognitionListener.save("GOODBYE");
        VoiceRecognitionListener.save("PROBLEMS");
        String home = System.getProperty("user.home");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        assertEquals("GOODBYE\n",
                readFile(home+"/Downloads/" + date + ".txt"));

    }

    @Test
    public void testSpeechResultHighestConfidence() throws IOException{
        List<String> list = new ArrayList<>();
        list.add("eating");
        list.add("swimming");
        list.add("walking");
        list.add("sleeping");
        float[] arr = new float[4];
        arr[0] = 0.991f;
        arr[1] = 0.897f;
        arr[2] = 0.564f;
        arr[3] = 0.209f;

        RepeatingService.ListenForVoice funcObj = new RepeatingService().new ListenForVoice();
        assertEquals("eating with a confidence level of 0.991",

                new VoiceRecognitionListener(funcObj).speechResult(list,arr));
        String home = System.getProperty("user.home");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        assertEquals("eating\n",
                readFile(home+"/Downloads/" + date + ".txt"));

    }

    @Test
    public void testSpeechResultMustBeDataSet() throws IOException{
        List<String> list = new ArrayList<>();
        list.add("swimming");
        list.add("swim");
        list.add("eating");
        list.add("walking");
        float[] arr = new float[4];
        arr[0] = 0.809f;
        arr[1] = 0.997f;
        arr[2] = 0.564f;
        arr[3] = 0.209f;
        RepeatingService.ListenForVoice funcObj = new RepeatingService().new ListenForVoice();

        assertEquals("swimming with a confidence level of 0.809",
                new VoiceRecognitionListener(funcObj).speechResult(list,arr));
        String home = System.getProperty("user.home");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());
        assertEquals("swimming\n",
                readFile(home+"/Downloads/" + date + ".txt"));

    }

    //  Method putExtra in android.content.Intent not mocked so fails and throws exception
    @Test (expected = RuntimeException.class)
    public void testSpeechResultListenAgain(){
        List<String> list = new ArrayList<>();
        list.add("dis");
        list.add("dat");
        list.add("eh");
        list.add("woot");
        float[] arr = new float[4];
        arr[0] = 0.809f;
        arr[1] = 0.997f;
        arr[2] = 0.564f;
        arr[3] = 0.209f;
        RepeatingService.ListenForVoice funcObj = new RepeatingService().new ListenForVoice();
        new VoiceRecognitionListener(funcObj).speechResult(list,arr);

    }

    // for reading in the file
    private String readFile(String filepath) throws FileNotFoundException, IOException {
        BufferedReader in;
        String temp;
        StringBuilder builder = new StringBuilder();
        try {
            in = new BufferedReader(new FileReader(filepath));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The file was not found. Please check the file path");
        }

        try {
            while ((temp = in.readLine()) != null) {
                builder.append(temp + "\n");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Something went wrong with IO");
        }


        return builder.toString();
    }

}