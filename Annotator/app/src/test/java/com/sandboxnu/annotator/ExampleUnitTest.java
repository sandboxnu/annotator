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
import java.util.Date;

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