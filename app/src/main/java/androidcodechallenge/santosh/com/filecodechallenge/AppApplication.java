package androidcodechallenge.santosh.com.filecodechallenge;

import android.app.Application;
import android.os.Handler;

import androidcodechallenge.santosh.com.filecodechallenge.controllers.DatabaseController;
import androidcodechallenge.santosh.com.filecodechallenge.controllers.SDCardController;

/**
 * Created by Santosh on 11/4/17.
 */

public class AppApplication extends Application {
    private static String TAG = AppApplication.class.getSimpleName();

    private AppAPI appAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseController databaseController = new DatabaseController(getApplicationContext());
        NotificationHandler notificationHandler = new NotificationHandler(getApplicationContext());
        SDCardController SDCardController = new SDCardController(getApplicationContext(),
                                                                    new Handler(),
                                                                    databaseController,
                                                                    notificationHandler);
        appAPI = new AppAPI(SDCardController, databaseController, notificationHandler);
    }

    public AppAPI getAppAPI() {
        return appAPI;
    }
}
