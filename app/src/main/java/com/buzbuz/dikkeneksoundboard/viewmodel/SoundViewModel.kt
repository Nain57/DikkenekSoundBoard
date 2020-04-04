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
package com.buzbuz.dikkeneksoundboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

import com.buzbuz.dikkeneksoundboard.database.Sound
import com.buzbuz.dikkeneksoundboard.database.SoundDao
import com.buzbuz.dikkeneksoundboard.database.SoundRoomDatabase

import kotlinx.coroutines.launch

/** ViewModel retrieving the database and exposing the sounds to the application. */
class SoundViewModel(application: Application) : AndroidViewModel(application) {

    /** Player managing the playback of the sounds. */
    private val soundPlayer = SoundPlayer(application)

    /** The data access object for the sound database. */
    val soundsDao: SoundDao
    /** LiveData upon the list of all sounds. */
    val allSounds: LiveData<List<Sound>>
    /** LiveData upon the list of favourite sounds. */
    val allFavouriteSounds: LiveData<List<Sound>>

    init {
        soundsDao = SoundRoomDatabase.getDatabase(application, viewModelScope).soundDao()
        allSounds = soundsDao.getAllSounds()
        allFavouriteSounds = soundsDao.getFavouriteSounds()
    }

    /**
     * Toggle the favourite state for the provided sound.
     * <p>
     * @param sound the sound to change the favourite state of.
     */
    fun toggleFavouriteState(sound: Sound) {
        val toggledSound = sound.copy(isFavourite = !sound.isFavourite)
        viewModelScope.launch { soundsDao.update(toggledSound) }
    }

    /**
     * Play the provided sound.
     * <p>
     * @param sound the sound to be played.
     */
    fun playSound(sound: Sound) {
        soundPlayer.play(sound)
    }

    /** Stop the currently playing sound, if any. */
    fun stopSound() {
        soundPlayer.stop()
    }
}