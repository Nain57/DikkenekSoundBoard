package com.buzbuz.dikkeneksoundboard;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SoundListCursorAdapter extends CursorAdapter {

    private int mTitleColumnIndex;

    private class ViewHolder {
        TextView mTitleView;
    }

    public SoundListCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.listitem_sound, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.mTitleView = (TextView) listItemView.findViewById(R.id.sound_title);
        listItemView.setTag(holder);

        return listItemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mTitleView.setText(cursor.getString(mTitleColumnIndex));
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            mTitleColumnIndex = newCursor.getColumnIndex(SoundDatabaseHelper.SoundColumn.COLUMN_NAME_TITLE);
        }
        return super.swapCursor(newCursor);
    }
}
