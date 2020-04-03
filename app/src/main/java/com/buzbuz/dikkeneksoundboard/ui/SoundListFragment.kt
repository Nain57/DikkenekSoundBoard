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
import androidx.fragment.app.ListFragment

/** Fragment displaying the list of sounds. */
class SoundListFragment : ListFragment() {

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
}