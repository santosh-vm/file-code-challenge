package android.santosh.com.filecodechallenge;

import android.santosh.com.filecodechallenge.controllers.DatabaseController;
import android.santosh.com.filecodechallenge.controllers.SDCardController;

/**
 * Created by Santosh on 11/4/17.
 */

public class AppAPI {
    private SDCardController SDCardController;
    private DatabaseController databaseController;

    AppAPI(SDCardController SDCardController, DatabaseController databaseController){
        this.SDCardController = SDCardController;
        this.databaseController = databaseController;
    }

    public SDCardController getSDCardController() {
        return SDCardController;
    }

    public DatabaseController getDatabaseController() {
        return databaseController;
    }
}
