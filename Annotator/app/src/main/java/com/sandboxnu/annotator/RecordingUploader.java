package com.sandboxnu.annotator;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jcraft.jsch.*;

import com.jcraft.jsch.ChannelSftp;

public class RecordingUploader {

    private String UPLOAD_ENDPOINT = "";
    private String SFTPHOST = "host:IP";
    private int SFTPPORT = 22;
    private String SFTPUSER = "username";
    private String SFTPPASS = "password";
    private String SFTPWORKINGDIR = "file/to/transfer";


    public void uploadAll() {
        try {
            // TODO: this is bad practice, remove later
            java.io.File test = new java.io.File("test.txt");
            File folder = test.getParentFile();
            File[] listOfFiles = folder.listFiles();

            if(listOfFiles == null) {
                // TODO: produce a warning or error here.

            } else {
                for(File file :  listOfFiles) {
                    // TODO: is this encrypted step necessary with SFTP?
                    File encrypted = this.encryptFile(file);
                    this.uploadFileSFTP(encrypted);
                    this.removeFile(encrypted);
                }
            }

        }catch(Exception e) {
            // TODO: catch exception in gui
            // throw new IllegalAccessException("Unable to access the files in the directory.");
        }
    }

    /**
     * Encrypts a file and returns its encrypted contents.
     * @param file the File to be encrypted.
     * @return the contents of the File, encrypted.
     */
    private String encryptFileString(File file) {
        String fileStr = this.parseFile(file);
        String pub_key = "";

        return "";
        // TODO: is this necessary?
    }

    /**
     * Replaces a file with an encrypted copy.
     * @param file the File to be encrypted.
     * @return the newly encrypted File.
     */
    private File encryptFile(File file) {
        // TODO: this method
        return file;
    }

    /**
     * Removes the given file from the device.
     * @param file the file to be removed.
     */
    private void removeFile(File file) {
        boolean success = file.delete();
        // TODO: assert that the file removal was a success in another way
        assert success;
    }

    /**
     * Parses the given file into its String represeentation.
     * @return the String representation of the encrypted file.
     */
    private String parseFile(File file) {
         try {
             FileInputStream fis = new FileInputStream(file);

            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            return new String(data, "UTF-8");
         } catch(Exception e) {
             // TODO: handle this exception instead of using dummy things
             // throw new IllegalAccessException("The file cannot be read.");
             return "";
         }
    }

    /**
     * Uploads the file to an SFTP server.
     * @param file the file to be uploaded.
     */
    private void uploadFileSFTP(File file) {
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            // create session with login information
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);

            // open channel
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "No");
            session.setConfig(config);
            session.connect();

            // connect with sftp
            channel = session.openChannel("sftp");
            channel.connect();

            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);

            // TODO: consider naming or renaming the file in a different fashion
            // for logging purposes.
            channelSftp.put(new FileInputStream(file), file.getName());

        } catch(Exception e) {
            // TODO: something when this fails
        } finally {
            channelSftp.exit();
            channel.disconnect();
            session.disconnect();
        }
    }

    /**
     * Uploads all of the given files to S3 using SFTP.
     * Unlike the other method, which makes one SFTP connection per file,
     * this one should make one SFTP connection per batch of files which
     * is very much preferred.
     * @param files the files to be transferred.
     */
    private void uploadFilesSFTP(File[] files) {
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            // create session with login information
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);

            // open channel
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "No");
            session.setConfig(config);
            session.connect();

            // connect with sftp
            channel = session.openChannel("sftp");
            channel.connect();

            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);

            // TODO: consider naming or renaming the file in a different fashion
            // for logging purposes.
            for(File file : files) {
                channelSftp.put(new FileInputStream(file), file.getName());
            }

        } catch(Exception e) {
            // TODO: something when this fails
        } finally {
            channelSftp.exit();
            channel.disconnect();
            session.disconnect();
        }
    }

    private void uploadFileHTTP(File file) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {

            // TODO: not sure what is going on here. would like to touch base during the meeting.
            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(UPLOAD_ENDPOINT);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name="uploaded_file";filename=""
                            + fileName + "" + lineEnd);

                    dos.writeBytes(lineEnd);
        }
        catch(Exception e) {

        }
    }

}
