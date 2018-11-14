package com.livewire.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.livewire.ui.fragments.HelpOfferedWorkerFragment;


/**
 * Created by mindiii on 11/14/18.
 */

public class MyJobWorkerFragmentAdapter extends FragmentPagerAdapter {

    private Activity activity;
    public MyJobWorkerFragmentAdapter(FragmentManager fm, FragmentActivity activity) {
        super(fm);
        this.activity = activity;
    }



    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return new HelpOfferedWorkerFragment();
        } else
            return new HelpOfferedWorkerFragment();
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
                return "Confirmed Jobs";
            case 1:
                return "Completed Jobs";
            default:
                return null;
        }

    }
}
