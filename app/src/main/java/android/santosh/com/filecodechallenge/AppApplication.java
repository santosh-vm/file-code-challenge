package android.santosh.com.filecodechallenge;

import android.app.Application;
import android.os.Handler;
import android.santosh.com.filecodechallenge.controllers.SDCardController;

/**
 * Created by Santosh on 11/4/17.
 */

public class AppApplication extends Application {
    private static String TAG = AppApplication.class.getSimpleName();

    private AppAPI appAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        SDCardController SDCardController = new SDCardController(getApplicationContext(),new Handler());
        appAPI = new AppAPI(SDCardController);
    }

    public AppAPI getAppAPI() {
        return appAPI;
    }
}
