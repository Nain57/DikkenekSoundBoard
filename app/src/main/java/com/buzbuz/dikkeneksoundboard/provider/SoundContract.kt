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

import android.net.Uri
import android.provider.BaseColumns

/** Authority of this Content provider. */
private const val AUTHORITY = "com.buzbuz.dikkeneksoundboard.contentprovider"
/** Base of the path containing all the sounds. */
private const val BASE_PATH = "sounds"

/** The URI used to find the files using the [SoundContentProvider] */
val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$BASE_PATH")

/** Class defining the columns available in the database  */
object SoundColumns {

    /**
     * Column containing the unique ID for a row.
     * Value will contains an Integer.
     */
    const val COLUMN_ID = BaseColumns._ID
    /**
     * Column containing the name of the sound used for display purpose.
     * Value will contains a String.
     */
    const val COLUMN_NAME_TITLE = "display_name"
    /**
     * Column containing the path of the file in the assets of the app.
     * Value will contains a String.
     */
    const val COLUMN_NAME_PATH = "path"
    /**
     * Column indicating if the sound is in the user favourites or not.
     * Value will contains an Integer, with 0 if the row is a not favorite, 1 if it is.
     */
    const val COLUMN_NAME_FAVOURITE = "favourites"

    /** Array of all columns available in the database  */
    val COLUMNS_LIST = arrayOf(
        COLUMN_ID,
        COLUMN_NAME_TITLE,
        COLUMN_NAME_PATH,
        COLUMN_NAME_FAVOURITE
    )
}

