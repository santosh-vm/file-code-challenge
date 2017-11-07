package androidcodechallenge.santosh.com.filecodechallenge;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.santosh.com.filecodechallenge.R;
import android.support.v4.app.NotificationCompat;

import androidcodechallenge.santosh.com.filecodechallenge.activity.MainActivity;

/**
 * Created by Santosh on 11/5/17.
 */

public class NotificationHandler {
    private static String TAG = NotificationHandler.class.getSimpleName();

    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificaionBuilder;
    private static int NOTIFICATION_ID = 1;

    NotificationHandler(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificaionBuilder = new NotificationCompat.Builder(context, "channel_id");
    }

    public void showProgressNotificaiton() {
        notificaionBuilder.setContentTitle("SD Card Reader.")
                .setContentText("SD card Read in progress")
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_sync_black_24dp)
                .setProgress(0, 0, true);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificaionBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notificaionBuilder.build());
    }

    public void showCompleteNotification(){
        notificaionBuilder.setContentTitle("SD Card Reader.")
                .setContentText("SD card Read complete.")
                .setAutoCancel(true)
                .setProgress(0, 0, false);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificaionBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, notificaionBuilder.build());
    }

    public void dismissNotificaiton(){
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
