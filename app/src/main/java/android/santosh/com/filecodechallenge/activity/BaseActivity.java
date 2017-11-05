package android.santosh.com.filecodechallenge.activity;

import android.os.Bundle;
import android.santosh.com.filecodechallenge.AppAPI;
import android.santosh.com.filecodechallenge.AppApplication;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Santosh on 11/4/17.
 */

public class BaseActivity extends AppCompatActivity {
    protected AppAPI appAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appAPI = ((AppApplication) getApplication()).getAppAPI();
    }
}
