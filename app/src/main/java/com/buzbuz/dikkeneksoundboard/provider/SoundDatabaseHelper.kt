/*
 * Copyright (C) 2020 Nain57
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */
package com.buzbuz.dikkeneksoundboard.provider

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log

/** SQLiteOpenHelper implementation managing the SQLite database containing all sounds informations. */
class SoundDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, "sounds.db", null, 1) {

    companion object {
        /** Tag for logs */
        private const val TAG = "SoundDatabaseHelper"

        /** The name of the table in the database containing all sounds. */
        private const val TABLE_NAME ="sounds"
        /** SQL request used to create the table of sounds in the database. */
        private const val SQL_CREATE_DATABASE = "CREATE TABLE $TABLE_NAME (" +
                "${SoundColumns.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${SoundColumns.COLUMN_NAME_TITLE} TEXT," +
                "${SoundColumns.COLUMN_NAME_PATH} TEXT," +
                "${SoundColumns.COLUMN_NAME_FAVOURITE} INTEGER )"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i(TAG, "Creating database")

        db!!.execSQL(SQL_CREATE_DATABASE)

        val soundPaths = context.assets.list("")?.filter { it.endsWith(".mp3") }
        if (!soundPaths.isNullOrEmpty()) {
            for (soundPath in soundPaths) {
                val value = ContentValues()
                value.put(SoundColumns.COLUMN_NAME_TITLE, formatSoundTitle(soundPath))
                value.put(SoundColumns.COLUMN_NAME_PATH, soundPath)
                value.put(SoundColumns.COLUMN_NAME_FAVOURITE, 0)
                db.insert(TABLE_NAME, null, value)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Not needed as they will be no newer sounds added to database
    }

    /**
     * Perform a query on the sound database.
     * <p>
     * @param projection a list of which columns to return.
     * @param selection filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE
     *                  itself). Passing null will return all rows for the given URL.
     * @param selectionArgs you may include ?s in selection, which will be replaced by the values from selectionArgs,
     *                      in order that they appear in the selection. The values will be bound as Strings.
     * @param sortOrder how to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
     *                  Passing null will use the default sort order, which may be unordered.
     * <p>
     * @return a cursor over the result set
     */
    fun queryDatabase(
        projection: Array<out String>,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = TABLE_NAME
        return queryBuilder.query(writableDatabase, projection, selection, selectionArgs, null, null, sortOrder)
    }

    /**
     * Format the title of a sound using its path.
     * <p>
     * @param path the path of the sound to format from.
     * <p>
     * @return the formatted title.
     */
    private fun formatSoundTitle(path: String) : String = path.substring(0, path.length - 4).replace('_', ' ')
}