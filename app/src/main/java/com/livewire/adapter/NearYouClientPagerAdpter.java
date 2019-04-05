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
import com.livewire.ui.fragments.NearYouClientFragment;
import com.livewire.ui.fragments.OngoingJobFragment;
import com.livewire.ui.fragments.SingleJobFragment;

/**
 * Created by mindiii on 4/2/19.
 */

public class NearYouClientPagerAdpter extends FragmentPagerAdapter {
    private Activity activity;
    private String message;
    private NearByResponce nearByResponce;

    public NearYouClientPagerAdpter(FragmentManager childFragmentManager, FragmentActivity activity, String message, NearByResponce nearByResponce) {
        super(childFragmentManager);
        this.activity = activity;
        this.nearByResponce = nearByResponce;
        this.message = message;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            /*InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
            return NearYouClientFragment.newInstance(message, nearByResponce);
        } else
            return MapviewClientFragment.newInstance(message, nearByResponce);
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
