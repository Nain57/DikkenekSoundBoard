package com.buzbuz.dikkeneksoundboard;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;

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

    private static final String SEARCH_SELECTION = SoundDatabaseHelper.SoundColumn.COLUMN_NAME_TITLE + " LIKE ?";

    /** Loader for the sound database cursor management */
    private final LoaderManager.LoaderCallbacks<Cursor> mSoundLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (TextUtils.isEmpty(mCurrentQuery)) {
                return new CursorLoader(getActivity(), SoundContentProvider.CONTENT_URI, PROJECTION, null, null, null);
            }
            return new CursorLoader(getActivity(), SoundContentProvider.CONTENT_URI, PROJECTION,
                    SEARCH_SELECTION, new String[] { mCurrentQuery + "%" }, null);
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

    private String mCurrentQuery = null;
    private final OnQueryTextListener mQueryListener = new OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                mCurrentQuery = null;
            } else {
                mCurrentQuery = newText;

            }
            getLoaderManager().restartLoader(0, null, mSoundLoaderCallbacks);
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        setListAdapter(new SoundListCursorAdapter(getActivity(), null, false));
        getLoaderManager().initLoader(LOADER_ID, null, mSoundLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setOnQueryTextListener(mQueryListener);
    }
}
