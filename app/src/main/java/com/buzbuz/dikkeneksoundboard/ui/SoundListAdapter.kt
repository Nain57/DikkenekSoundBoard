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
package com.buzbuz.dikkeneksoundboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.buzbuz.dikkeneksoundboard.R
import com.buzbuz.dikkeneksoundboard.database.Sound

import kotlinx.android.synthetic.main.item_sound.view.title
import kotlinx.android.synthetic.main.item_sound.view.btn_fav

/**
 * Adapter displaying a list of sounds.
 * <p>
 * @param soundClickListener notified upon a click on the sound item
 * @param favouriteClickListener notified upon a click on the favourite checkbox of a sound item
 */
class SoundListAdapter(
    private val soundClickListener: (Sound) -> Unit,
    private val favouriteClickListener: (Sound) -> Unit
) : RecyclerView.Adapter<SoundViewHolder>() {

    /** The list of sounds to be shown by this adapter. */
    var sounds: List<Sound>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = sounds?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder =
        SoundViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sound, parent, false))

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) =
        holder.bind(sounds!![position], soundClickListener, favouriteClickListener)
}

/**
 * View holder displayed by the [SoundListAdapter].
 * <p>
 * @param itemView the root view for the item in the list
 */
class SoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * Bind this view holder to the sound to be represented.
     * <p>
     * @param sound the sound this holder is representing
     * @param soundClickListener notified upon a click on the sound item
     * @param favouriteClickListener notified upon a click on the favourite checkbox of a sound item
     */
    fun bind(sound: Sound, soundClickListener: (Sound) -> Unit, favouriteClickListener: (Sound) -> Unit) {
        itemView.title.apply {
            text = sound.name
            setOnClickListener { soundClickListener.invoke(sound) }
        }
        itemView.btn_fav.apply {
            isChecked = sound.isFavourite
            setOnClickListener { favouriteClickListener.invoke(sound) }
        }
    }
}