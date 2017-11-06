package android.santosh.com.filecodechallenge.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.santosh.com.filecodechallenge.R;
import android.santosh.com.filecodechallenge.listerners.SDCardControllerListener;
import android.santosh.com.filecodechallenge.model.FileExtensionVO;
import android.santosh.com.filecodechallenge.model.FileNameVO;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener, SDCardControllerListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;

    private Button startButton;
    private Button stopButton;
    private View progressViewRoot;
    private TextView progressMessageTextView;
    private View detailsRootView;
    private TextView averageFileSizeTextView;

    private LinearLayout topFileSizeLinearLayout;
    private LinearLayout frequentFileExtensionLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        toggleViews();
        addListeners();
    }

    private void bindViews() {

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        progressViewRoot = findViewById(R.id.progress_view_root_layout);
        progressMessageTextView = findViewById(R.id.progress_message);

        detailsRootView = findViewById(R.id.details_root_layout);
        averageFileSizeTextView = findViewById(R.id.average_file_size_textview);
        topFileSizeLinearLayout = findViewById(R.id.get_top_file_list);
        frequentFileExtensionLinearLayout = findViewById(R.id.get_top_file_extension_list);
    }

    private void toggleViews() {
        invalidateOptionsMenu();
        if (appAPI.getSDCardController().isThreadActive()) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            progressViewRoot.setVisibility(View.VISIBLE);
            detailsRootView.setVisibility(View.GONE);
        } else {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            progressViewRoot.setVisibility(View.GONE);
            if (appAPI.getDatabaseController().getFileListRecordCount() > 0
                    && appAPI.getDatabaseController().getFileExtensionListRecordCount() > 0) {
                appAPI.getDatabaseController().getTotalFilesSize();
                detailsRootView.setVisibility(View.VISIBLE);
                setAverageTextView();
                buildDetails();
            } else {
                detailsRootView.setVisibility(View.GONE);
            }
        }
    }

    private void addListeners() {
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        appAPI.getSDCardController().addSdCardControllerListener(this);
    }

    private void removeListeners() {
        startButton.setOnClickListener(null);
        stopButton.setOnClickListener(null);
        appAPI.getSDCardController().removeSdCardControllerListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (appAPI.getSDCardController().isThreadActive()) {
            appAPI.getSDCardController().stopDirectoryParsing();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = new ShareActionProvider(this);
        MenuItemCompat.setActionProvider(item, shareActionProvider);
        shareActionProvider.setShareIntent(createShareIntent());
        if (!appAPI.getSDCardController().isThreadActive() && (appAPI.getDatabaseController().getFileListRecordCount() > 0
                && appAPI.getDatabaseController().getFileExtensionListRecordCount() > 0)) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        return true;
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        long totalFileSize = appAPI.getDatabaseController().getTotalFilesSize();
        long fileCount = appAPI.getDatabaseController().getFileListRecordCount();

        double averageFileSize = 0D;
        if (totalFileSize > 0 && fileCount > 0) {
            averageFileSize = (totalFileSize / fileCount) / (1024D * 1024D);
        }

        // Add data to the intent, the receiving app will decide what to do with it.
        intent.putExtra(Intent.EXTRA_SUBJECT, "File Statistics");
        //We can send whatever data we want here. For now I'm sharing only average file size.
        intent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.US, "Average File size: %.2f MB", averageFileSize));
        return intent;
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

    @Override
    public void onParseStart() {
        invalidateOptionsMenu();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        progressViewRoot.setVisibility(View.VISIBLE);
        detailsRootView.setVisibility(View.GONE);
    }

    @Override
    public void onParseProgress(String message) {
        progressMessageTextView.setText(String.format(Locale.US, "%s", message));
    }

    @Override
    public void onParseStop() {
        invalidateOptionsMenu();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        progressViewRoot.setVisibility(View.GONE);
        if (appAPI.getDatabaseController().getFileListRecordCount() > 0
                && appAPI.getDatabaseController().getFileExtensionListRecordCount() > 0) {
            detailsRootView.setVisibility(View.VISIBLE);
            setAverageTextView();
            buildDetails();
        } else {
            detailsRootView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onParseFinish() {
        invalidateOptionsMenu();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        progressViewRoot.setVisibility(View.GONE);
        if (appAPI.getDatabaseController().getFileListRecordCount() > 0
                && appAPI.getDatabaseController().getFileExtensionListRecordCount() > 0) {
            detailsRootView.setVisibility(View.VISIBLE);
            setAverageTextView();
            buildDetails();
        } else {
            detailsRootView.setVisibility(View.GONE);
        }
    }

    private void setAverageTextView() {
        long totalFileSize = appAPI.getDatabaseController().getTotalFilesSize();
        long fileCount = appAPI.getDatabaseController().getFileListRecordCount();
        double averageFileSize = (totalFileSize / fileCount) / (1024D * 1024D);
        averageFileSizeTextView.setText(String.format(Locale.US, "%.2f MB", averageFileSize));
    }

    private void buildDetails() {
        List<FileNameVO> fileNameVOList = appAPI.getDatabaseController().getTopSizeFilesByCount(10);
        topFileSizeLinearLayout.removeAllViews();
        for (FileNameVO fileNameVO : fileNameVOList) {
            View child = getLayoutInflater().inflate(R.layout.linear_layout_list_item, null);
            TextView titleText = child.findViewById(R.id.title_text);
            TextView detailsTextView = child.findViewById(R.id.details_text);
            titleText.setText(fileNameVO.getFileName());
            double fileSizeInMB = fileNameVO.getFileSize() / (1024D * 1024D);
            detailsTextView.setText(String.format(Locale.US, "%.2f MB", fileSizeInMB));
            topFileSizeLinearLayout.addView(child);
        }
        List<FileExtensionVO> fileExtensionVOList = appAPI.getDatabaseController().getTopFileExtensionsByCount(5);
        frequentFileExtensionLinearLayout.removeAllViews();
        for (FileExtensionVO fileExtensionVO : fileExtensionVOList) {
            View child = getLayoutInflater().inflate(R.layout.linear_layout_list_item, null);
            TextView titleText = child.findViewById(R.id.title_text);
            TextView detailsTextView = child.findViewById(R.id.details_text);
            titleText.setText(fileExtensionVO.getFileExtenion());
            detailsTextView.setText(String.format(Locale.US, "%d", fileExtensionVO.getCount()));
            frequentFileExtensionLinearLayout.addView(child);
        }
    }
}
