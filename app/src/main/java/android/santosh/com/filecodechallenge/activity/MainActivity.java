package android.santosh.com.filecodechallenge.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.santosh.com.filecodechallenge.R;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;

    private Button startButton;
    private Button stopButton;
    private View progressViewRoot;
    private View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        addListeners();
    }

    private void bindViews() {
        loadingView = findViewById(R.id.full_screen_loading_view);
        //TODO: Remove this
        loadingView.setVisibility(View.GONE);

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        progressViewRoot = findViewById(R.id.progress_view_root_layout);
    }

    private void toggleViews(){

    }

    private void addListeners() {
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    private void removeListeners() {
        startButton.setOnClickListener(null);
        stopButton.setOnClickListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                if (Build.VERSION.SDK_INT <= 22) {
                    appAPI.getSDCardController().beginDirectoryParsing(Environment.getExternalStorageDirectory());
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    } else {
                        appAPI.getSDCardController().beginDirectoryParsing(Environment.getExternalStorageDirectory());
                    }
                }


                break;
            case R.id.stop_button:
                Log.d(TAG,"stop button pressed.");
                appAPI.getSDCardController().stopDirectoryParsing();
                break;
            default:

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted Hurray!");
                    // permission was granted, yay! Do the
                    // SD card related task you need to do.
                    appAPI.getSDCardController().beginDirectoryParsing(Environment.getExternalStorageDirectory());
                } else {
                    finish();
                    Toast.makeText(this, "Read permission is required to access SD card", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }
            break;
            // other 'case' lines to check for other
            // permissions this app might request
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }

    }
}
