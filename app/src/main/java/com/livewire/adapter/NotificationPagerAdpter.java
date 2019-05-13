package com.livewire.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.List;

public class NotificationPagerAdpter extends FragmentPagerAdpter {
    private List<Fragment> fragmentList;
    private List<String> headerList;
    public NotificationPagerAdpter(FragmentManager fm, FragmentActivity activity, List<Fragment> fragmentList, List<String> headerList) {
        super(fm, activity);
        this.fragmentList = fragmentList;
        this.headerList = headerList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
    @Override
    public Fragment getItem(int position) {
      /*  if (position == 0) {
            *//*InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*//*
            return fragmentList.get(0);
        } else*/
            return fragmentList.get(position);
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
                return headerList.get(position);
    }

}
