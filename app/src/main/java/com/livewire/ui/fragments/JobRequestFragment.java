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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.adapter.JobRequestFragmentAdapter;
import com.livewire.adapter.MyJobWorkerFragmentAdapter;
import com.livewire.utils.Constant;


public class JobRequestFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context mContext;


    public JobRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_request, container, false);
    }


        @Override
        public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.setFocusableInTouchMode(true);
            view.setClickable(true);
            view.requestFocus();
            viewPager = view.findViewById(R.id.viewpager);

            JobRequestFragmentAdapter adapter = new JobRequestFragmentAdapter(getChildFragmentManager(),getActivity());
            viewPager.setAdapter(adapter);
            tabLayout = view.findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_black_shorterm);
            tabLayout.getTabAt(0).setTag("ShortTerm");
            tabLayout.getTabAt(1).setTag("LongTerm");
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_onging_job );

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
                tabTextView.setAllCaps(false);
                tabTextView.setTypeface(tf);
            }


            /// hide soft keyboard in view pager """""""""//
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
                   if (tab.getTag().equals("ShortTerm"))
                    tab.setIcon(R.drawable.ic_black_shorterm);
                   else tab.setIcon(R.drawable.ic_active_ongoing);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    if (tab.getTag().equals("ShortTerm"))
                        tab.setIcon(R.drawable.ic_inactive_shorterm);
                    else tab.setIcon(R.drawable.ic_onging_job);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    /*this method is not used*/
                }
            });
        }

}
