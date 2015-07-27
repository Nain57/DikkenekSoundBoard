package com.buzbuz.dikkeneksoundboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.IOException;

/**
 * Class offering helping functions to access and modify the sound database
 */
public class SoundDatabaseHelper extends SQLiteOpenHelper {

    /** Tag for logs */
    private static final String LOG_TAG = "SoundDatabaseHelper";

    /** Helper static class containing all columns available in the database */
    public static abstract class SoundColumn implements BaseColumns {
        /** Column containing the name of the sound used for display purpose */
        public static final String COLUMN_NAME_TITLE = "_display_name";
        /** Column containing the path of the file in the assets of the app */
        public static final String COLUMN_NAME_PATH = "path";
        /** Column indicating if the sound is in the user favourites or not */
        public static final String COLUMN_NAME_FAVOURITE = "favourites";
        /** Nullable column (see Android doc) */
        public static final String COLUMN_NAME_NULLABLE = "nullable";

        /** Array of all columns available in the database */
        public static final String[] COLUMNS_LIST = {
                _ID,
                COLUMN_NAME_TITLE,
                COLUMN_NAME_PATH,
                COLUMN_NAME_FAVOURITE,
                COLUMN_NAME_NULLABLE
        };
    }
    /** Name of the table containing all sounds */
    public static final String TABLE_NAME = "sounds";

    private static final String TEXT_TYPE = "TEXT";
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String SQL_CREATE_DATABASE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SoundColumn._ID + " INTEGER PRIMARY KEY," +
                    SoundColumn.COLUMN_NAME_TITLE + " " + TEXT_TYPE + "," +
                    SoundColumn.COLUMN_NAME_PATH + " " + TEXT_TYPE + "," +
                    SoundColumn.COLUMN_NAME_FAVOURITE + " " + INTEGER_TYPE +
            " )";

    private static final String DATABASE_NAME = "sounds.db";
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    public SoundDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DATABASE);
        populate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not needed as they will be no newer sounds added to database
    }

    /**
     * List all sounds files from the assets and fill the database with it.
     * Must be called at first launch of app
     */
    private void populate(SQLiteDatabase db) {
        try {
            String[] assetsList = mContext.getAssets().list("");
            if (assetsList.length > 0) {
                for (String soundFile : assetsList) {
                    if (soundFile.endsWith(".mp3")) {
                        ContentValues rowValues = new ContentValues();
                        rowValues.put(SoundColumn.COLUMN_NAME_TITLE, formatSoundTitle(soundFile));
                        rowValues.put(SoundColumn.COLUMN_NAME_PATH, soundFile);
                        rowValues.put(SoundColumn.COLUMN_NAME_FAVOURITE, 0);
                        db.insert(TABLE_NAME, SoundColumn.COLUMN_NAME_NULLABLE, rowValues);
                    }
                }
            } else {
                Log.e(LOG_TAG, "Can't find any sound files from assets root folder");
            }
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Can't get sound list from assets");
        }
    }

    private String formatSoundTitle(String filePath) {
        String result;
        result = filePath.substring(0, filePath.length()-4);
        result = result.replace('_', ' ');
        return result;
    }
}
