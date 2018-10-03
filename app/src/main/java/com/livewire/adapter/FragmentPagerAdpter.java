package com.livewire.adapter;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.livewire.ui.fragments.OngoingJobFragment;
import com.livewire.ui.fragments.SingleJobFragment;

/**
 * Created by mindiii on 9/28/18.
 */

public class FragmentPagerAdpter extends FragmentPagerAdapter {


    public FragmentPagerAdpter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SingleJobFragment();
        } else
            return new OngoingJobFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SINGLE PROJECT / TASK";
            case 1:
                return "ONGOING HELP NEEDED";
            default:
                return null;
        }

    }
}
