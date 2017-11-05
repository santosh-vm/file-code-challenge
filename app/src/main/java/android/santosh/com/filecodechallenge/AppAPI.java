package android.santosh.com.filecodechallenge;

import android.santosh.com.filecodechallenge.controllers.SDCardController;

/**
 * Created by Santosh on 11/4/17.
 */

public class AppAPI {
    private SDCardController SDCardController;

    AppAPI(SDCardController SDCardController){
        this.SDCardController = SDCardController;
    }

    public SDCardController getSDCardController() {
        return SDCardController;
    }
}
