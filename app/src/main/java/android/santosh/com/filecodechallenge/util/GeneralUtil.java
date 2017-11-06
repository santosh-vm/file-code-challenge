package android.santosh.com.filecodechallenge.util;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by Santosh on 11/5/17.
 */

public class GeneralUtil {
    public static String TAG = GeneralUtil.class.getSimpleName();

    public static void closeClosable(final Cursor cursor) {
        if (cursor == null) {
            return;
        }
        try {
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "GeneralUtil -> closeClosable():" + e);
        }
    }
}
