package android.santosh.com.filecodechallenge.listerners;

/**
 * Created by Santosh on 11/5/17.
 */

public interface SDCardControllerListener {
    void onParseStart();

    void onParseProgress();

    void onParseStop();

    void onParseFinish();
}