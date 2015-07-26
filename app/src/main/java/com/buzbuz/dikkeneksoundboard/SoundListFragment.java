package com.buzbuz.dikkeneksoundboard;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import android.os.Bundle;


/**
 * List fragment used to display the list of sounds
 */
public class SoundListFragment extends ListFragment {

    /** Tag for logs */
    private static final String LOG_TAG = "SoundListFragment";

    /** Loader id */
    private static final int LOADER_ID = 1;

    /** Projection used for the data base request */
    private static final String[] PROJECTION = {
        SoundDatabaseHelper.SoundColumn._ID,
        SoundDatabaseHelper.SoundColumn.COLUMN_NAME_TITLE,
        SoundDatabaseHelper.SoundColumn.COLUMN_NAME_PATH,
        SoundDatabaseHelper.SoundColumn.COLUMN_NAME_FAVOURITE
    };

    /** Loader for the sound database cursor management */
    private final LoaderManager.LoaderCallbacks<Cursor> mSoundLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), SoundContentProvider.CONTENT_URI, PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            ((SoundListCursorAdapter) getListAdapter()).swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            ((SoundListCursorAdapter) getListAdapter()).swapCursor(null);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        setListAdapter(new SoundListCursorAdapter(getActivity(), null, false));
        getLoaderManager().initLoader(LOADER_ID, null, mSoundLoaderCallbacks);
    }
}
