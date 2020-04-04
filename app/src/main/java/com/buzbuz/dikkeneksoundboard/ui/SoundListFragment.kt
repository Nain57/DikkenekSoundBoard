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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

import com.buzbuz.dikkeneksoundboard.R
import com.buzbuz.dikkeneksoundboard.SoundViewModel
import com.buzbuz.dikkeneksoundboard.database.Sound

/** Fragment displaying the list of sounds. */
class SoundListFragment : Fragment() {

    companion object {

        /** Key for the Fragment argument Bundle telling if we should show only the favourites sounds or all sounds. */
        private const val ARG_IS_FAVOURITES = "isFavourites"

        /**
         * Instantiates a new SoundListFragment.
         * <p>
         * @param isFavourites true to instantiates the fragment displaying the favourites sound list, false for all.
         * <p>
         * @return the new fragment instance.
         */
        @JvmStatic
        fun newInstance(isFavourites: Boolean): SoundListFragment {
            return SoundListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_FAVOURITES, isFavourites)
                }
            }
        }
    }

    /** View shown when there is no sounds available. */
    private lateinit var emptyView: TextView
    /** View displaying the list of sounds. */
    private lateinit var soundsView: RecyclerView
    /** View shown when the [observer] hasn't been notified yet. */
    private lateinit var loadingView: ProgressBar
    /** View model providing the sounds. */
    private lateinit var soundModel: SoundViewModel
    /** Adapter for [soundsView] responsible of showing the sounds as a list. */
    private lateinit var soundAdapter: SoundListAdapter

    /** Tells if this fragment is for displaying the favourites sounds. */
    private var isFavourites = false

    /** Observer upon the list of sounds to be displayed by this fragment. */
    private val observer: Observer<List<Sound>> = Observer { sounds ->
        loadingView.visibility = View.GONE
        if (sounds.isNullOrEmpty()) {
            soundsView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            soundsView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }

        soundAdapter.sounds = sounds
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_sound, container, false)

        soundsView = rootView.findViewById(R.id.list)
        loadingView = rootView.findViewById(R.id.progress)
        emptyView = rootView.findViewById(R.id.empty)

        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isFavourites = arguments?.getBoolean(ARG_IS_FAVOURITES) ?: false
        soundModel = ViewModelProvider(this).get(SoundViewModel::class.java)
        soundAdapter = SoundListAdapter(soundModel::playSound, soundModel::toggleFavouriteState)

        if (isFavourites) {
            soundModel.allFavouriteSounds.observe(this, observer)
        } else {
            soundModel.allSounds.observe(this, observer)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soundsView.adapter = soundAdapter
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFavourites) {
            soundModel.allFavouriteSounds.removeObservers(this)
        } else {
            soundModel.allSounds.removeObservers(this)
        }
        soundModel.stopSound()
    }
}