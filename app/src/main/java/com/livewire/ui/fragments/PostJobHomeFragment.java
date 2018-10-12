package com.livewire.ui.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.adapter.FragmentPagerAdpter;
import com.livewire.utils.Constant;

/**
 * Created by mindiii on 9/28/18.
 */

public class PostJobHomeFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_job_home, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusableInTouchMode(true);
        view.setClickable(true);
        view.requestFocus();
        viewPager = view.findViewById(R.id.viewpager);
        FragmentPagerAdpter adapter = new FragmentPagerAdpter(getChildFragmentManager(),getActivity());
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // decrease width of indicator
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(15, 0, 15, 0);
            tab.requestLayout();
        }

        final Typeface tf = ResourcesCompat.getFont(mContext,R.font.poppins_medium);
        final Typeface rl = ResourcesCompat.getFont(mContext,R.font.poppins_regular);


        //""""" Zero position selected so change fontFamily """""""""""//
        for (int i=0;i<tabLayout.getTabCount();i++){
            LinearLayout tabLayout1 = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
            tabTextView.setTypeface(tf);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Constant.hideSoftKeyBoard(mContext,viewPager);
            }

            @Override
            public void onPageSelected(int position) {
                // blank
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            // blank
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            /*this method is not used*/
            }
        });
    }
}
