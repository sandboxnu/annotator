package com.sandboxnu.annotator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

// used for sftp
import com.jcraft.jsch.*;

// used for aes
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import com.jcraft.jsch.ChannelSftp;

import javax.crypto.SecretKeyFactory;

public class RecordingUploader {

    private String UPLOAD_ENDPOINT = "";
    private String SFTPHOST = "host:IP";
    private int SFTPPORT = 22;
    private String SFTPUSER = "username";
    private String SFTPPASS = "password";
    private String SFTPWORKINGDIR = "file/to/transfer";


    public void uploadAll() {
        try {
            // TODO: this is bad practice, remove once we know file path
            java.io.File test = new java.io.File("test.txt");
            File folder = test.getParentFile();
            // delete the test file right away as it is used to find the directory
            // test.delete();

            if(folder != null) {
                File[] listOfFiles = folder.listFiles();

                if (listOfFiles == null) {
                    // TODO: produce a warning or error here.
                    throw new IllegalArgumentException("There are no files to send.");
                } else {
                    for (File file : listOfFiles) {
                        // TODO: is this encrypted step necessary with SFTP?
                        File encrypted = this.encryptFile(file);
                        this.uploadFileSFTP(encrypted);
                        this.removeFile(encrypted);
                    }
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
        byte[] contents = Base64.encodeBase64(this.encryptFileBytes(file));
        if(contents != null) {
            return new String(contents);
        } else {
            throw new IllegalArgumentException("The file could not be parsed.");
        }
    }

    /**
     * Replaces a file with an encrypted copy.
     * @param file the File to be encrypted.
     * @return the newly encrypted File.
     */
    private File encryptFile(File file) {
        if (file.exists()) {
            try {

                String contents = this.encryptFileString(file);
                boolean deleteSuccess = file.delete();

                boolean success = file.createNewFile();

                if (!success || !deleteSuccess) {
                    throw new IllegalAccessException("Cannot access file!");
                } else {
                    FileOutputStream outputStream = new FileOutputStream(file);

                    outputStream.write(Base64.decodeBase64(contents));

                    return file;
                }
            } catch (Exception e) {
                // TODO: throw exception
                e.printStackTrace();
            }
        }

        // TODO: this is bad = the file should always be non-null
        // find a way to throw exceptions here and handle them displayed to the user
        return null;
    }

    /**
     * Encrypts a file with AES.
     * @param file the file to be encrypted.
     * @return a byte array containing the encrypted file.
     */
    private byte[] encryptFileBytes(File file) {
        try {
            String keyValue = "abc";
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            KeySpec spec = new PBEKeySpec(keyValue.toCharArray(), Hex.decodeHex("dc0da04af8fee58593442bf834b30739"), 5);

            Key key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex("dc0da04af8fee58593442bf834b30739")));

            // read in file as String
            String fileString = this.parseFile(file);

            // finally encrypt everything
            return c.doFinal(fileString.getBytes());
        } catch (Exception e) {
            // TODO: handle exception
        }

        return null;
    }

    /**
     * Removes the given file from the device.
     * @param file the file to be removed.
     */
    private void removeFile(File file) {
        boolean success = file.delete();
        // TODO: assert that the file removal was a success in another way
        if(!success) {
            throw new IllegalArgumentException("The file could not be deleted.");
        }
    }

    /**
     * Parses the given file into its String representation.
     * @return the String representation of the encrypted file.
     */
    private String parseFile(File file) {
         try {
             /*
             FileInputStream fis = new FileInputStream(file);

            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            return new String(data, "UTF-8");*/

             InputStream inputStream = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

             StringBuilder stringBuilder = new StringBuilder();

             boolean done = false;

             // reads lines from the file one by one while they are not null
             // unsure if this implementation or the one above is more efficient
             while(!done) {
                 String line = reader.readLine();
                 done = (line == null);

                 if (line != null) {
                     stringBuilder.append(line);
                 }
             }

             reader.close();
             inputStream.close();

             return stringBuilder.toString();

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
            // TODO: something when this fails, pref. throw our own exception
            // which displays an informative error for the user.
        } finally {
            try {
                channelSftp.exit();
                channel.disconnect();
                session.disconnect();
            } catch(Exception e){
                e.printStackTrace();
            }
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
            try {
                channelSftp.exit();
                channel.disconnect();
                session.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        int maxBufferSize = 1024 * 1024;

        try {

            // TODO: not sure what is going on here. would like to touch base during the meeting.
            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(UPLOAD_ENDPOINT);

            String fileName = file.getName();
            String uploaded_file = fileName + "";

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
            dos.writeBytes("Content-Disposition: form-data; name=" + uploaded_file + ";filename="
                            + fileName + "" + lineEnd);

                    dos.writeBytes(lineEnd);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
