package android.santosh.com.filecodechallenge.controllers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Santosh on 11/4/17.
 */

public class SDCardController {
    private static String TAG = SDCardController.class.getSimpleName();
    private static final double MEGABYTE = 1024D * 1024D;
    private boolean isThreadActive = false;

    private Context context;
    private Handler handler;
    private Thread thread;

    public SDCardController(Context context, Handler handler) {
        Log.d(TAG, "SDCardController Initialized");
        this.context = context;
        this.handler = handler;
    }

    public void beginDirectoryParsing(final File rootFolder) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isThreadActive = true;
                    parseDirectory(rootFolder);
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException block: "+e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    public void stopDirectoryParsing() {
        isThreadActive = false;
    }

    public boolean isThreadActive() {
        return isThreadActive;
    }

    private void parseDirectory(File directory) throws InterruptedException {
        File[] listFile = directory.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                //Log.d(TAG, "parseDirectory Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted()+", Thread.currentThread().getId(): "+Thread.currentThread().getId());
                if (Thread.currentThread().isInterrupted()) {
                    //Log.d(TAG, "Thread interrupted let break from here.");
                    break;
                }
                if (listFile[i].isDirectory()) {
                    parseDirectory(listFile[i]);
                } else {
                    if (!isThreadActive && !Thread.currentThread().isInterrupted()) {
                        //Log.d(TAG, "THREAD INTERRUPT, Thread.currentThread().getID(): " + Thread.currentThread().getId());
                        Thread.currentThread().interrupt();
                        thread = null;
                        return;
                    }
                    //Log.d(TAG, "listFile[i].getName(): " + listFile[i].getName());
                    //File name and File size in bytes.
                    double fileSizeinMB = listFile[i].length() / MEGABYTE;
                    //Log.d(TAG, "listfile name:" + listFile[i].getName() + ", listFile[i].length(): " + listFile[i].length() + ", fileSizeinMB: " + String.format("%.4f", fileSizeinMB));
                    //TODO: Save file name into file table
                    Pattern pattern = Pattern.compile("(.*)(\\.)(.*)");
                    Matcher matcher = pattern.matcher(listFile[i].getName());
                    if (matcher.matches() && matcher.groupCount() > 0) {
                        int groupCount = matcher.groupCount();
                        for (int j = 1; j <= groupCount; j++) {
                            if (j == groupCount) {
                                //Log.d(TAG, "group: " + i + ", text: " + matcher.group(j));
                                //TODO: save to database the file extension type.
                            }
                        }
                    }
                }

            }
        }

    }

}
