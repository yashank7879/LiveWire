package com.livewire.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.livewire.responce.NearByResponce;
import com.livewire.ui.fragments.MapviewClientFragment;
import com.livewire.ui.fragments.MapviewWorkerFragment;
import com.livewire.ui.fragments.NearYouClientFragment;
import com.livewire.ui.fragments.NearYouWorkerFragment;

/**
 * Created by mindiii on 4/2/19.
 */

public class NearYouWorkerPagerAdpter extends FragmentPagerAdapter {
    private Activity activity;
    private String message;
    private NearByResponce nearByResponce;

    public NearYouWorkerPagerAdpter(FragmentManager childFragmentManager, FragmentActivity activity, String message, NearByResponce nearByResponce) {
        super(childFragmentManager);
        this.activity = activity;
        this.nearByResponce = nearByResponce;
        this.message = message;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return  NearYouWorkerFragment.newInstance(message,nearByResponce);
        } else
            return  MapviewWorkerFragment.newInstance(message,nearByResponce);
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
                return "List View";
            case 1:
                return "Map View";
            default:
                return null;
        }

    }
}
