package androidcodechallenge.santosh.com.filecodechallenge.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import androidcodechallenge.santosh.com.filecodechallenge.database.DatabaseHelper;
import androidcodechallenge.santosh.com.filecodechallenge.database.DatabaseLender;
import androidcodechallenge.santosh.com.filecodechallenge.model.FileExtensionVO;
import androidcodechallenge.santosh.com.filecodechallenge.model.FileNameVO;
import androidcodechallenge.santosh.com.filecodechallenge.util.GeneralUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Santosh on 11/5/17.
 */

public class DatabaseController {
    private static String TAG = DatabaseController.class.getSimpleName();

    private final DatabaseLender databaseLender;
    private Context context;

    public DatabaseController(Context context) {
        databaseLender = DatabaseLender.getInstance(context);
        this.context = context;
    }

    public void resetFileListTable() {
        SQLiteDatabase sqLiteDatabase = databaseLender.openDatabase();
        DatabaseHelper.resetFileListTable(sqLiteDatabase);
        databaseLender.closeDatabase();
    }

    public boolean saveFileDetails(String fileName, long fileSize) {
        final ContentValues values = DatabaseHelper.getFileListContent(fileName, fileSize);
        final SQLiteDatabase db = databaseLender.openDatabase();
        boolean success = false;

        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "saveFileDetails, whoa, SQLiteDatabase == null!.");
            return false;
        }
        try {
            int result = 0;
            result += db.update(DatabaseHelper.TABLE_NAME_FILE_LIST, values, DatabaseHelper.COLUMN_FILE_NAME + " IS ?", new String[]{String.valueOf(fileName)});

            if (result > 0) {
                success = true;
            } else {
                // Update failed or wasn't possible, insert instead
                final long id = db.insert(DatabaseHelper.TABLE_NAME_FILE_LIST, null, values);

                if (id > -1) {
                    success = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "saveFileDetails, Exception:" + e.toString());
        } finally {
            databaseLender.closeDatabase();
        }

        return success;
    }

    public void resetFileExtensionListTable() {
        SQLiteDatabase sqLiteDatabase = databaseLender.openDatabase();
        DatabaseHelper.resetFileExtensionListTable(sqLiteDatabase);
        databaseLender.closeDatabase();
    }

    public boolean saveFileExtensionDetails(String fileExtension, int count) {
        final ContentValues values = DatabaseHelper.getFileExtensionListContent(fileExtension, count);
        final SQLiteDatabase db = databaseLender.openDatabase();
        boolean success = false;

        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "saveFileExtensionDetails, whoa, SQLiteDatabase == null!.");
            return false;
        }
        try {
            int result = 0;
            result += db.update(DatabaseHelper.TABLE_NAME_FILE_EXTENSION_LIST,
                    values,
                    DatabaseHelper.COLUMN_FILE_EXTENSION + " IS ?",
                    new String[]{String.valueOf(fileExtension)});

            if (result > 0) {
                success = true;
            } else {
                // Update failed or wasn't possible, insert instead
                final long id = db.insert(DatabaseHelper.TABLE_NAME_FILE_EXTENSION_LIST, null, values);

                if (id > -1) {
                    success = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "saveFileExtensionDetails, Exception:" + e.toString());
        } finally {
            databaseLender.closeDatabase();
        }

        return success;
    }

    public FileExtensionVO getFileExtensionVOByFileExtension(String fileExtension) {
        FileExtensionVO fileExtensionVO = null;
        final SQLiteDatabase db = databaseLender.openDatabase();
        Cursor cursor = null;
        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "whoa, getFileExtensionVOByFileExtension(), db == null SQLiteDatabase == null!");
            return null;
        }

        try {
            cursor = db.query(DatabaseHelper.TABLE_NAME_FILE_EXTENSION_LIST,
                    DatabaseHelper.FILE_EXTENSION_LIST_FIELDS,
                    DatabaseHelper.COLUMN_FILE_EXTENSION + "=?",
                    new String[]{String.valueOf(fileExtension)},
                    null,
                    null,
                    null,
                    " 1");

            if (cursor == null || cursor.isAfterLast()) {
                return null;
            }
            //Log.d(TAG, "getFileExtensionVOByFileExtension() size " + cursor.getCount());
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                fileExtensionVO = DatabaseHelper.getFileExtensionFromCursor(cursor);
            }

        } catch (Exception e) {
            Log.e(TAG, "getFileExtensionVOByFileExtension(), Exception:" + e);
        } finally {
            GeneralUtil.closeClosable(cursor);
            databaseLender.closeDatabase();
        }

        return fileExtensionVO;
    }

    public List<FileNameVO> getTopSizeFilesByCount(int count){
        List<FileNameVO> fileNameVOList = new ArrayList<>();

        final SQLiteDatabase db = databaseLender.openDatabase();
        Cursor cursor = null;
        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "whoa, getTopSizeFilesByCount(), db == null SQLiteDatabase == null!");
            return fileNameVOList;
        }
        try {
            cursor = db.query(DatabaseHelper.TABLE_NAME_FILE_LIST,
                    DatabaseHelper.FILE_LIST_FIELDS,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_FILE_SIZE + " DESC",
                    " "+count);

            if (cursor == null || cursor.isAfterLast()) {
                return fileNameVOList;
            }
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    FileNameVO fileNameVO = DatabaseHelper.getFileSizeFromCursor(cursor);
                    fileNameVOList.add(fileNameVO);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getTopSizeFilesByCount(), Exception:" + e);
        } finally {
            GeneralUtil.closeClosable(cursor);
            databaseLender.closeDatabase();
        }

        return fileNameVOList;
    }

    public List<FileExtensionVO> getTopFileExtensionsByCount(int count){
        List<FileExtensionVO> fileExtensionVOList = new LinkedList<>();

        final SQLiteDatabase db = databaseLender.openDatabase();
        Cursor cursor = null;
        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "whoa, getTopSizeFilesByCount(), db == null SQLiteDatabase == null!");
            return fileExtensionVOList;
        }
        try {
            cursor = db.query(DatabaseHelper.TABLE_NAME_FILE_EXTENSION_LIST,
                    DatabaseHelper.FILE_EXTENSION_LIST_FIELDS,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_FILE_EXTENSION_COUNT + " DESC",
                    " "+count);

            if (cursor == null || cursor.isAfterLast()) {
                return fileExtensionVOList;
            }
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    FileExtensionVO fileExtensionVO = DatabaseHelper.getFileExtensionFromCursor(cursor);
                    fileExtensionVOList.add(fileExtensionVO);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getTopSizeFilesByCount(), Exception:" + e);
        } finally {
            GeneralUtil.closeClosable(cursor);
            databaseLender.closeDatabase();
        }

        return fileExtensionVOList;
    }

    public long getFileListRecordCount() {
        long recordCount = 0;
        final SQLiteDatabase db = databaseLender.openDatabase();
        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "whoa, getFileListRecordCount(), db == null SQLiteDatabase == null!");
            return 0;
        }
        try {
            recordCount = DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_NAME_FILE_LIST);
        } catch (Exception e) {
            Log.e(TAG, "getFileListRecordCount(), Exception:" + e);
        } finally {
            databaseLender.closeDatabase();
        }

        return recordCount;
    }

    public long getFileExtensionListRecordCount() {
        long recordCount = 0;
        final SQLiteDatabase db = databaseLender.openDatabase();
        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "whoa, getFileExtensionListRecordCount(), db == null SQLiteDatabase == null!");
            return 0;
        }
        try {
            recordCount = DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_NAME_FILE_EXTENSION_LIST);
        } catch (Exception e) {
            Log.e(TAG, "getFileExtensionListRecordCount(), Exception:" + e);
        } finally {
            databaseLender.closeDatabase();
        }

        return recordCount;
    }

    public long getTotalFilesSize() {
        long totaleFileSize = 0;
        final SQLiteDatabase db = databaseLender.openDatabase();
        Cursor cursor = null;
        if (db == null) {
            databaseLender.closeDatabase();
            Log.e(TAG, "whoa, getTotalFilesSize(), db == null SQLiteDatabase == null!");
            return 0L;
        }

        try {
            cursor = db.query(DatabaseHelper.TABLE_NAME_FILE_LIST,
                    new String[]{String.valueOf(DatabaseHelper.COLUMN_FILE_SIZE)},
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor == null || cursor.isAfterLast()) {
                return 0L;
            }
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    totaleFileSize = totaleFileSize + cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_FILE_SIZE));
                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getTotalFilesSize(), Exception:" + e);
        } finally {
            GeneralUtil.closeClosable(cursor);
            databaseLender.closeDatabase();
        }
        return totaleFileSize;
    }

}
