package com.buzbuz.dikkeneksoundboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SoundListCursorAdapter extends CursorAdapter {

    private boolean mDisplayFavourites;
    private int mIdColumnIndex;
    private int mTitleColumnIndex;
    private int mFavouriteColumnIndex;

    private class ViewHolder {
        TextView mTitleView;
        CheckBox mFavouritesCheckBox;
    }

    public SoundListCursorAdapter(Context context, Cursor c, boolean autoRequery, boolean favourites) {
        super(context, c, autoRequery);
        mDisplayFavourites = favourites;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.listitem_sound, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.mTitleView = (TextView) listItemView.findViewById(R.id.sound_title);
        holder.mFavouritesCheckBox = (CheckBox) listItemView.findViewById(R.id.btn_fav);
        if (!mDisplayFavourites) {
            holder.mFavouritesCheckBox.setVisibility(View.GONE);
        }
        listItemView.setTag(holder);

        return listItemView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mTitleView.setText(cursor.getString(mTitleColumnIndex));

        if (mDisplayFavourites) {
            final int id = cursor.getInt(mIdColumnIndex);
            final int favValue = cursor.getInt(mFavouriteColumnIndex);
            holder.mFavouritesCheckBox.setChecked(favValue == 1);
            holder.mFavouritesCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues values = new ContentValues();
                    values.put(SoundDatabaseHelper.SoundColumn.COLUMN_NAME_FAVOURITE, favValue == 0 ? 1 : 0);

                    context.getContentResolver().update(SoundContentProvider.CONTENT_URI,
                            values,
                            SoundDatabaseHelper.SoundColumn._ID + "=" + id,
                            null);
                }
            });
        }
    }


    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            mIdColumnIndex = newCursor.getColumnIndex(SoundDatabaseHelper.SoundColumn._ID);
            mTitleColumnIndex = newCursor.getColumnIndex(SoundDatabaseHelper.SoundColumn.COLUMN_NAME_TITLE);
            mFavouriteColumnIndex = newCursor.getColumnIndex(SoundDatabaseHelper.SoundColumn.COLUMN_NAME_FAVOURITE);
        }
        return super.swapCursor(newCursor);
    }
}
