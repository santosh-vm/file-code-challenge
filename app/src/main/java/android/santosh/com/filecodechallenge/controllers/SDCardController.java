package android.santosh.com.filecodechallenge.controllers;

import android.content.Context;
import android.os.Handler;
import android.santosh.com.filecodechallenge.NotificationHandler;
import android.santosh.com.filecodechallenge.listerners.SDCardControllerListener;
import android.santosh.com.filecodechallenge.model.FileExtensionVO;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Santosh on 11/4/17.
 */

public class SDCardController {
    private static String TAG = SDCardController.class.getSimpleName();
    private static final double MEGABYTE = 1024D * 1024D;


    private Context context;
    private Handler handler;
    private Thread thread;
    private boolean isThreadActive = false;
    private List<SDCardControllerListener> sdCardControllerListeners = Collections.synchronizedList(new ArrayList<SDCardControllerListener>());
    private DatabaseController databaseController;
    private NotificationHandler notificationHandler;

    public SDCardController(Context context, Handler handler,
                            DatabaseController databaseController,
                            NotificationHandler notificationHandler) {
        this.context = context;
        this.handler = handler;
        this.databaseController = databaseController;
        this.notificationHandler = notificationHandler;
    }

    public void beginDirectoryParsing(final File rootFolder) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyOnParseStart();
                    databaseController.resetFileListTable();
                    databaseController.resetFileExtensionListTable();
                    notificationHandler.showProgressNotificaiton();
                    isThreadActive = true;
                    parseDirectory(rootFolder);
                    isThreadActive = false;
                    notificationHandler.dismissNotificaiton();
                    notificationHandler.showCompleteNotification();
                    notifyOnParseFinish();
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException block: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    public void stopDirectoryParsing() {
        isThreadActive = false;
        notifyOnParseStop();
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
                        notificationHandler.dismissNotificaiton();
                        notificationHandler.showCompleteNotification();
                        return;
                    }
                    //File name and File size in bytes.
                    notifyOnParseProgress(listFile[i].getName());
                    double fileSizeinMB = listFile[i].length() / MEGABYTE;
                    //Log.d(TAG,"fileSizeinMB:: "+String.format("%.4f", fileSizeinMB));
                    boolean isFileInsertSuccess = databaseController.saveFileDetails(listFile[i].getName(), listFile[i].length());

                    Pattern pattern = Pattern.compile("(.*)(\\.)(.*)");
                    Matcher matcher = pattern.matcher(listFile[i].getName());
                    if (matcher.matches() && matcher.groupCount() > 0) {
                        int groupCount = matcher.groupCount();
                        for (int j = 1; j <= groupCount; j++) {
                            if (j == groupCount) {
                                String fileExtension = matcher.group(j).toLowerCase();
                                int fileExtensionCount = 0;
                                FileExtensionVO fileExtensionVO = databaseController.getFileExtensionVOByFileExtension(fileExtension);
                                if(fileExtensionVO!=null){
                                    fileExtensionCount = fileExtensionVO.getCount() + 1;
                                }else{
                                    fileExtensionCount  = 1;
                                }
                                boolean isFileExtensionInsertSuccess = databaseController.saveFileExtensionDetails(fileExtension,fileExtensionCount);
                            }
                        }
                    }
                }

            }
        }
    }

    public void addSdCardControllerListener(SDCardControllerListener sdCardControllerListener) {
        if (sdCardControllerListeners != null && !sdCardControllerListeners.contains(sdCardControllerListener)) {
            sdCardControllerListeners.add(sdCardControllerListener);
        }
    }

    public void removeSdCardControllerListener(SDCardControllerListener sdCardControllerListener) {
        if (sdCardControllerListener != null && sdCardControllerListeners.contains(sdCardControllerListener)) {
            sdCardControllerListeners.remove(sdCardControllerListener);
        }
    }

    private void notifyOnParseStart() {
        if (sdCardControllerListeners != null && sdCardControllerListeners.size() > 0) {
            for (final SDCardControllerListener sdCardControllerListener : sdCardControllerListeners) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sdCardControllerListener.onParseStart();
                    }
                });
            }
        }
    }

    private void notifyOnParseStop() {
        if (sdCardControllerListeners != null && sdCardControllerListeners.size() > 0) {
            for (final SDCardControllerListener sdCardControllerListener : sdCardControllerListeners) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sdCardControllerListener.onParseStop();
                    }
                });
            }
        }
    }

    private void notifyOnParseProgress(final String message) {
        if (sdCardControllerListeners != null && sdCardControllerListeners.size() > 0) {
            for (final SDCardControllerListener sdCardControllerListener : sdCardControllerListeners) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sdCardControllerListener.onParseProgress(message);
                    }
                });
            }
        }
    }

    private void notifyOnParseFinish() {
        if (sdCardControllerListeners != null && sdCardControllerListeners.size() > 0) {
            for (final SDCardControllerListener sdCardControllerListener : sdCardControllerListeners) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sdCardControllerListener.onParseFinish();
                    }
                });
            }
        }
    }

}
