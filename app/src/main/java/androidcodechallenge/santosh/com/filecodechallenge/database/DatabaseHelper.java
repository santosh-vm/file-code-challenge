package androidcodechallenge.santosh.com.filecodechallenge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidcodechallenge.santosh.com.filecodechallenge.model.FileExtensionVO;
import androidcodechallenge.santosh.com.filecodechallenge.model.FileNameVO;
import android.util.Log;

/**
 * Created by Santosh on 11/5/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String TAG = DatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "file_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onUpgrade, db: " + db);
        createFileListTable(db);
        createFileExtensionListTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade, db: " + db + ", oldVersion: " + oldVersion + ", newVersion: " + newVersion);
    }

    static void createFileListTable(SQLiteDatabase db) {
        db.execSQL(DatabaseHelper.CREATE_TABLE_FILE_LIST);
    }

    public static void resetFileListTable(SQLiteDatabase db) {
        try {
            db.execSQL("drop table if exists " + DatabaseHelper.TABLE_NAME_FILE_LIST);
            createFileListTable(db);
        } catch (Exception e) {
            Log.e(TAG, "resetFileListTable: " + e.toString());
        }
    }

    public static void createFileExtensionListTable(SQLiteDatabase db) {
        db.execSQL(DatabaseHelper.CREATE_TABLE_FILE_EXTENSION_LIST);
    }

    public static void resetFileExtensionListTable(SQLiteDatabase db) {
        try {
            db.execSQL("drop table if exists " + DatabaseHelper.TABLE_NAME_FILE_EXTENSION_LIST);
            createFileExtensionListTable(db);
        } catch (Exception e) {
            Log.e(TAG, "resetFileExtensionListTable: " + e.toString());
        }
    }

    //================================================================================
    // File List Object
    //================================================================================
    public static final String TABLE_NAME_FILE_LIST = "FileList";
    //Columns
    public static final String COLUMN_FILE_INDEX = "_id";
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_FILE_SIZE = "file_size";

    public static final String[] FILE_LIST_FIELDS = {COLUMN_FILE_NAME, COLUMN_FILE_SIZE};
    /**
     * The SQL code that creates FileList table which will hold list of files
     */
    public static final String CREATE_TABLE_FILE_LIST =
            "CREATE TABLE " + TABLE_NAME_FILE_LIST + "("
                    + COLUMN_FILE_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + COLUMN_FILE_NAME + " TEXT,"
                    + COLUMN_FILE_SIZE + " INTEGER"
                    + ")";

    public static ContentValues getFileListContent(String fileName, long fileSize) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_NAME, fileName);
        values.put(COLUMN_FILE_SIZE, fileSize);
        return values;
    }

    //================================================================================
    // File Extension Object
    //================================================================================
    public static final String TABLE_NAME_FILE_EXTENSION_LIST = "FileExtensionList";
    //Columns
    public static final String COLUMN_FILE_EXTENSION_INDEX = "_id";
    public static final String COLUMN_FILE_EXTENSION = "file_extension";
    public static final String COLUMN_FILE_EXTENSION_COUNT = "file_extension_count";

    public static final String[] FILE_EXTENSION_LIST_FIELDS = {COLUMN_FILE_EXTENSION, COLUMN_FILE_EXTENSION_COUNT};
    /**
     * The SQL code that creates FileList table which will hold list of files
     */
    public static final String CREATE_TABLE_FILE_EXTENSION_LIST =
            "CREATE TABLE " + TABLE_NAME_FILE_EXTENSION_LIST + "("
                    + COLUMN_FILE_EXTENSION_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + COLUMN_FILE_EXTENSION + " TEXT,"
                    + COLUMN_FILE_EXTENSION_COUNT + " INTEGER"
                    + ")";

    public static ContentValues getFileExtensionListContent(String fileExtension, int count) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_EXTENSION, fileExtension);
        values.put(COLUMN_FILE_EXTENSION_COUNT, count);
        return values;
    }

    public static FileNameVO getFileSizeFromCursor(final Cursor cursor) {
        FileNameVO fileNameVO = new FileNameVO();
        fileNameVO.setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME)));
        fileNameVO.setFileSize(cursor.getLong(cursor.getColumnIndex(COLUMN_FILE_SIZE)));
        return fileNameVO;
    }

    public static FileExtensionVO getFileExtensionFromCursor(final Cursor cursor) {
        FileExtensionVO fileExtensionVO = new FileExtensionVO();
        fileExtensionVO.setCount(cursor.getInt(cursor.getColumnIndex(COLUMN_FILE_EXTENSION_COUNT)));
        fileExtensionVO.setFileExtenion(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_EXTENSION)));
        return fileExtensionVO;
    }
}
