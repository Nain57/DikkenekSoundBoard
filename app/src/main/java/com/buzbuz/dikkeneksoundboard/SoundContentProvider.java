package com.buzbuz.dikkeneksoundboard;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Content provider for the sounds in the assets using the created database
 * (see {@link com.buzbuz.dikkeneksoundboard.SoundDatabaseHelper})
 */
public class SoundContentProvider extends ContentProvider {

    /** Tag for logs */
    private static final String LOG_TAG = "SoundContentProvider";

    /** Authority of the content provider */
    private static final String AUTHORITY = "com.buzbuz.dikkeneksoundboard.contentprovider";
    /** Base path of the content provider */
    private static final String BASE_PATH = "sounds";
    /** The URI used to find the files using this content provider */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, BASE_PATH, 1);
    }

    private static final String[] OPEN_FILE_PROJECTION = {
            SoundDatabaseHelper.SoundColumn._ID,
            SoundDatabaseHelper.SoundColumn.COLUMN_NAME_PATH
    };

    private SoundDatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new SoundDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        checkColumns(projection);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SoundDatabaseHelper.TABLE_NAME);

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "audio/mp3";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int rowsUpdated = db.update(SoundDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
    }

    private void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(SoundDatabaseHelper.SoundColumn.COLUMNS_LIST));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection " + requestedColumns.toString());
            }
        }
    }
}
