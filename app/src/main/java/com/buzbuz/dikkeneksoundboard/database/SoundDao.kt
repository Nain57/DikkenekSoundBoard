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
package com.buzbuz.dikkeneksoundboard.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/** Data access object for the sound database matching SQL queries to methods. */
@Dao
interface SoundDao {

    /**
     * Get all sounds in the database.
     * <p>
     * @return the [LiveData] on the list of sounds.
     */
    @Query("SELECT * from sound_table ORDER BY name ASC")
    fun getAllSounds(): LiveData<List<Sound>>

    /**
     * Get the list of sounds marked as favourites in the database.
     * <p>
     * @return the [LiveData] on the list of favourite sounds.
     */
    @Query("SELECT * from sound_table WHERE isFavourite = 1 ORDER BY name ASC")
    fun getFavouriteSounds(): LiveData<List<Sound>>

    /**
     * Get a list of sounds filtered on the name.
     * <p>
     * @param filter the filter to be applied.
     * <p>
     * @return the filtered list.
     */
    @Query("SELECT * FROM sound_table WHERE name LIKE '%' || :filter || '%' ORDER BY name ASC")
    fun getFilteredSounds(filter: String): LiveData<List<Sound>>

    /**
     * Insert a sound in the database.
     * <p>
     * @param sound the sound to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sound: Sound)

    /**
     * Update a sound in the database. Used to change the favourite state of a sound.
     * <p>
     * @param sound the sound to be updated.
     */
    @Update
    suspend fun update(sound: Sound)
}