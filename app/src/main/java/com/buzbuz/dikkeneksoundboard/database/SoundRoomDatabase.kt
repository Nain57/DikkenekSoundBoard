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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** The room database for the sounds, populating the database upon first creation. */
@Database(entities = [Sound::class], version = 1, exportSchema = false)
abstract class SoundRoomDatabase : RoomDatabase() {

    /** The data access object for the sound database. */
    abstract fun soundDao(): SoundDao

    companion object {

        /** Singleton preventing multiple instances of database opening at the same time. */
        @Volatile
        private var INSTANCE: SoundRoomDatabase? = null

        /**
         * Get the Room database singleton, or instantiates it if it wasn't yet.
         * <p>
         * @param context the Android context.
         * <p>
         * @return the Room database singleton.
         */
        fun getDatabase(context: Context, scope: CoroutineScope): SoundRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundRoomDatabase::class.java,
                    "sound_database")
                    .addCallback(SoundDatabaseCallback(context, scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callbacks upon the Room database lifecycle.
     * <p>
     * Upon database first creation, it will populate it with all the sounds found in the assets folder.
     */
    private class SoundDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val soundPaths = context.assets.list("")?.filter { it.endsWith(".mp3") }
            if (soundPaths.isNullOrEmpty()) {
                return
            }

            INSTANCE?.let { database ->
                scope.launch {
                    val soundDao = database.soundDao()
                    for (soundPath in soundPaths) {
                        soundDao.insert(Sound(0, formatSoundTitle(soundPath), soundPath, false))
                    }
                }
            }
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
}