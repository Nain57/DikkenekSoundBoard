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

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build

import com.buzbuz.dikkeneksoundboard.database.Sound

/**
 * Player for the sounds.
 * <p>
 * Provides method to start and stop the sound playback, as well as the management of the audio focus.
 * <p>
 * @param context the Android context.
 */
class SoundPlayer(private val context: Context) {

    /** Android media player playing the sounds. */
    private val mediaPlayer = MediaPlayer()
    /** Android audio manager managing the audio focus. */
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    /** Audio attributes for the player and audio focus. */
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()
    /** Audio focus request for Android versions above O. */
    private val audioFocusRequest: AudioFocusRequest?
    /** Listener upon the audio focus state changes. */
    private val audioFocusListener = this::onAudioFocusChanged

    init {
        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.setOnCompletionListener { abandonAudioFocus() }

        audioFocusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(audioFocusListener)
                .build()
            } else {
                null
            }
    }

    /**
     * Play the provided sound, if the audio focus can be granted.
     * <p>
     * @param sound the sound to be played.
     */
    fun play(sound: Sound) {
        stop()

        if (requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return
        }

        context.assets.openFd(sound.path).use { soundFd ->
            mediaPlayer.reset()
            mediaPlayer.setDataSource(soundFd.fileDescriptor, soundFd.startOffset, soundFd.length)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    /** Stop the currently playing sound, if any. */
    fun stop() {
        if (mediaPlayer.isPlaying) {
            abandonAudioFocus()
            mediaPlayer.stop()
        }
    }

    /**
     * Request the audio focus.
     * <p>
     * This method handles the focus request pre and post Android O.
     * <p>
     * @return the audio focus request result. Can be either {@link #AUDIOFOCUS_REQUEST_FAILED} or
     *         {@link #AUDIOFOCUS_REQUEST_GRANTED}.
     */
    private fun requestAudioFocus(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION") // Needed to handle focus for Android versions below O
            audioManager.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        }

    /**
     * Abandon the audio focus.
     * <p>
     * This method handles the focus abandon pre and post Android O.
     */
    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION") // Needed to handle focus for Android versions below O
            audioManager.abandonAudioFocus(audioFocusListener)
        }
    }

    /**
     * Handle the changes on the focus.
     * <p>
     * As all sounds have a really short duration, we always stop the playback for all kind of loss, and ignore any
     * gain notifications.
     * <p>
     * @param focusChange the type of change on the audio focus.
     */
    private fun onAudioFocusChanged(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> stop()
            else -> return
        }
    }
}