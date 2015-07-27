package com.buzbuz.dikkeneksoundboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

    /** Number of pages in Pager */
    private static final int PAGE_COUNT = 2;
    /** Name of tabs */
    private static final String mTabTitles[] = new String[] { "Favoris", "Tous" };

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return SoundListFragment.newInstance(position == 0);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

}
