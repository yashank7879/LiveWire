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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.adapter.FragmentPagerAdpter;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusableInTouchMode(true);
        view.setClickable(true);
        view.requestFocus();
        viewPager = view.findViewById(R.id.viewpager);
        FragmentPagerAdpter adapter = new FragmentPagerAdpter(getChildFragmentManager());
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
       /* LinearLayout tabLayout1 = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
        tabTextView.setTextSize(22);
        tabTextView.setTypeface(tf);*/
        //"""""""" if User select another Top Tab so change fontFamily """""""'//
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
              /*  for (int i=0;i<tabLayout.getTabCount();i++){
                        LinearLayout tabLayout1 = (LinearLayout) ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                        TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
                        tabTextView.setTextSize(20);
                        tabTextView.setTypeface(tf);
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            /*this method is not used*/
            }
        });
       // tabLayout.setupWithViewPager();
    }
}
