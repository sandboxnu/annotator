package com.sandboxnu.annotator;

import java.io.File;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;


/**
 * Represents a function object (with helpers) to handle the uploading of the data recorded.
 * Typical usage is 'RecordingUploader.uploadAll()' to upload all of the stored CSV files.
 * In the future, this will require some path to the default destination for the files.
 */
class RecordingUploader {

    // Stores a reference to the main activity.
    private MainActivity app;

    /**
     * Instantiates an uploader able to track files.
     * @param app a reference to our application's MainActivity to reference the app's context.
     */
    RecordingUploader(MainActivity app) {
        this.app = app;
    }

    // Instantiates a reference to the s3 client with the locally stored credentials.
    private final AmazonS3 s3Client = new AmazonS3Client(
            new BasicAWSCredentials(
                    BuildConfig.AWS_PUB,
                    BuildConfig.AWS_SECRET
            )
    );

    /**
     * Uploads all of the files stored locally to an S3 bucket,
     * then deletes the local copies of the files.
     */
    void uploadAll() {
        System.out.println("Uploading files to S3...");
        try {

            // obtain all of the files
            File[] listOfFiles = this.app.getFilesDir().listFiles();

            if (listOfFiles == null) {
                throw new IllegalArgumentException("There are no files to send.");
            } else {
                // upload all of the files
                uploadFilesS3(listOfFiles);
            }

        } catch (Exception e) {
            // if the file could not be uploaded, throw an exception
            throw new IllegalStateException("Unable to access the files in the directory.");
        }
    }

    /**
     * Upload a series of files to an S3 bucket.
     * Better than uploading a single file, as it requires only a single S3 connection.
     *
     * @param files the files to upload to S3.
     */
    private void uploadFilesS3(File[] files) {

        // construct the transfer utility with the s3 client
        TransferUtility tfUtility =
                TransferUtility.builder()
                        .context(this.app.getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        // there may be a way to upload multiple objects at once to S3
        for (final File file : files) {
            // put an object intoto s3 with the information
            TransferObserver tfObserver = tfUtility.upload(
                    "annotated-data/" + file.getName(),
                    file,
                    CannedAccessControlList.PublicRead);

            // figure out when the file has been uploaded,
            // deleting the file after it uploads
            // only onStateChanges is necessary,
            // but to support deleting the files after upload we must implement one.
            tfObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    System.out.println(state);
                    if (TransferState.COMPLETED == state) {
                        removeFile(file);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    System.out.println("Progress changed... " + bytesCurrent
                            + " out of " + bytesTotal + ".");
                }

                @Override
                public void onError(int id, Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Removes the given file from the device.
     *
     * @param file the file to be removed.
     */
    private static void removeFile(File file) {
        boolean success = file.delete();
        if (!success) {
            throw new IllegalArgumentException("The file could not be deleted.");
        }
    }
}