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

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

import java.lang.IllegalArgumentException

/** Content provider exposing the access to the sound database to the application. */
class SoundContentProvider : ContentProvider() {

    companion object {
        /** Mp3 MIME type. */
        private const val MP3_MIME_TYPE = "audio/mp3"
    }

    /** Helper creating the database and managing the queries. */
    private lateinit var databaseHelper: SoundDatabaseHelper

    override fun onCreate(): Boolean {
        databaseHelper = SoundDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        if (projection == null || projection.any { !SoundColumns.COLUMNS_LIST.contains(it) }) {
            throw IllegalArgumentException("Invalid projection")
        }

        val cursor = databaseHelper.queryDatabase(projection, selection, selectionArgs, sortOrder)
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(p0: Uri): String? = MP3_MIME_TYPE

    override fun insert(p0: Uri, p1: ContentValues?): Uri? =
        throw UnsupportedOperationException("Insertion is not allowed")

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int =
        throw UnsupportedOperationException("Deletion is not allowed")
}