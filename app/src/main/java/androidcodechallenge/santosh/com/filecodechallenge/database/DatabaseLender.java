package androidcodechallenge.santosh.com.filecodechallenge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by Santosh on 11/5/17.
 */

public class DatabaseLender {

    private static final String TAG = DatabaseLender.class.getName();

    /**
     * Singleton object
     */
    private static DatabaseLender instance;
    /**
     * DatabaseHelper object managed by this class
     */
    final private DatabaseHelper dbHelper;
    /**
     * Current connection count
     */
    private volatile int dbConnections;

    private static final boolean ENABLE_LOGGING = false;

    private DatabaseLender(final Context appContext) {
        dbHelper = new DatabaseHelper(appContext);
        dbConnections = 0;
    }

    /**
     * Create or return an instance of DatabaseLender
     *
     * @param context context
     * @return
     */
    synchronized public static DatabaseLender getInstance(final Context context) {
        if (instance == null) {
            instance = new DatabaseLender(context);
        }
        return instance;
    }

    /**
     * Always call closeDatabase() when you are done with the db you opened here
     *
     * @return
     */
    public SQLiteDatabase openDatabase() {
        // Count the connection and return a new db
        synchronized (dbHelper) {
            if(ENABLE_LOGGING) {
                Log.d(TAG, "openDatabase: dbConnections: " + dbConnections);
            }

            try {
                final SQLiteDatabase database = dbHelper.getWritableDatabase();
                dbConnections++;
                return database;
            } catch(SQLiteException e) {
                Log.e(TAG, "openDatabase(): "+e.toString());
            }

            return null;
        }
    }

    /**
     * Notify about finishing stuff with db opened in openDatabase()
     */
    public void closeDatabase() {
        if (dbHelper == null) {
            Log.d(TAG, "dbHelper = null @ closeDatabase(), nothing to close");
            return;
        }
        // Count the disconnect and if we are at zero, then close all connections
        synchronized (dbHelper) {
            if(ENABLE_LOGGING) {
                Log.d(TAG, "closeDatabase: dbConnections: " + dbConnections);
            }

            dbConnections--;
            if (dbConnections == 0) {
                if(ENABLE_LOGGING) {
                    Log.d(TAG, "closeDatabase: dbHelper closed");
                }
                dbHelper.close();
            }
        }
    }
}
