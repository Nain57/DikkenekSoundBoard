package com.buzbuz.dikkeneksoundboard;

import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import java.io.IOException;

/**
 * List fragment used to display the list of sounds
 */
public class SoundListFragment extends ListFragment {

    /** Tag for logs */
    private static final String LOG_TAG = "SoundListFragment";

    /** Arguments for fragment creation */
    private static final String ARG_FAVOURITES = "arg_favourite";

    /** Loader id */
    private static final int LOADER_ID = 1;

    /** Projection used for the data base request */
    private static final String[] PROJECTION = {
        SoundDatabaseHelper.SoundColumn._ID,
        SoundDatabaseHelper.SoundColumn.COLUMN_NAME_TITLE,
        SoundDatabaseHelper.SoundColumn.COLUMN_NAME_PATH,
        SoundDatabaseHelper.SoundColumn.COLUMN_NAME_FAVOURITE
    };

    private static final String FAVOURITES_SELECTION = SoundDatabaseHelper.SoundColumn.COLUMN_NAME_FAVOURITE + " = ? ";
    private static final String SEARCH_SELECTION = SoundDatabaseHelper.SoundColumn.COLUMN_NAME_TITLE + " LIKE ?";

    private Cursor mCurrentCursor;
    private int mSoundPathColumnIndex = -1;
    /** Loader for the sound database cursor management */
    private final LoaderManager.LoaderCallbacks<Cursor> mSoundLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mIsFavourites && !TextUtils.isEmpty(mCurrentQuery)) {
                return new CursorLoader(getActivity(), SoundContentProvider.CONTENT_URI, PROJECTION,
                        FAVOURITES_SELECTION + " AND " + SEARCH_SELECTION, new String[] { "1", mCurrentQuery + "%" }, null);
            } else if (mIsFavourites) {
                return new CursorLoader(getActivity(), SoundContentProvider.CONTENT_URI, PROJECTION,
                        FAVOURITES_SELECTION, new String[] { "1" }, null);
            } else if (!TextUtils.isEmpty(mCurrentQuery)) {
                return new CursorLoader(getActivity(), SoundContentProvider.CONTENT_URI, PROJECTION,
                        SEARCH_SELECTION, new String[] { mCurrentQuery + "%" }, null);
            }
            return new CursorLoader(getActivity(), SoundContentProvider.CONTENT_URI, PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCurrentCursor = data;
            mSoundPathColumnIndex = mCurrentCursor.getColumnIndex(SoundDatabaseHelper.SoundColumn.COLUMN_NAME_PATH);
            ((SoundListCursorAdapter) getListAdapter()).swapCursor(mCurrentCursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCurrentCursor = null;
            mSoundPathColumnIndex = -1;
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

    private MediaPlayer mPlayer;
    private boolean mIsFavourites;

    /**
     * Create a new instance of the sound list fragment
     * @param favourites Display the favourite start
     * @return The preformated SoundListFragment
     */
    public static SoundListFragment newInstance(boolean favourites) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_FAVOURITES, favourites);
        SoundListFragment fragment = new SoundListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsFavourites = getArguments().getBoolean(ARG_FAVOURITES);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(!mIsFavourites);

        setListAdapter(new SoundListCursorAdapter(getActivity(), null, false, !mIsFavourites));
        getLoaderManager().initLoader(LOADER_ID, null, mSoundLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setOnQueryTextListener(mQueryListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPlayer.isPlaying()) mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCurrentCursor != null) {
            int oldPosition = mCurrentCursor.getPosition();
            mCurrentCursor.moveToPosition(position);
            playSelectedSound(mCurrentCursor.getString(mSoundPathColumnIndex));
            mCurrentCursor.moveToPosition(oldPosition);
        }
    }

    private void playSelectedSound(String filePath) {
        if (mPlayer.isPlaying()) mPlayer.stop();

        try {
            AssetFileDescriptor soundFileDescriptor = getActivity().getAssets().openFd(filePath);
            mPlayer.reset();
            mPlayer.setDataSource(soundFileDescriptor.getFileDescriptor(),
                                  soundFileDescriptor.getStartOffset(),
                                  soundFileDescriptor.getLength());
            mPlayer.prepare();
            mPlayer.setVolume(1f, 1f);
            mPlayer.start();
            soundFileDescriptor.close();
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Can't find sound file from asset " + filePath);
        }
    }
}
