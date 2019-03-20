package com.livewire.ui.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.adapter.FragmentPagerAdpter;
import com.livewire.adapter.MyJobWorkerFragmentAdapter;
import com.livewire.utils.Constant;

/**
 * Created by mindiii on 9/28/18.
 */

public class MyJobWorkerFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_job_worker, container, false);
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
        MyJobWorkerFragmentAdapter adapter = new MyJobWorkerFragmentAdapter(getChildFragmentManager(),getActivity());
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        View actionBar = view.findViewById(R.id.action_bar);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);
        header.setText(liveWireText(mContext));
        ImageView ivFilter = actionBar.findViewById(R.id.iv_filter);
        ivFilter.setVisibility(View.GONE);





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

    public static SpannableStringBuilder liveWireText(Context mContext) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString Name1 = new SpannableString("Live");
        Name1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorGreen)), 0, Name1.length(), 0);
        builder.append(Name1);
        SpannableString interesString = new SpannableString(" Wire");
        interesString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, interesString.length(), 0);
        builder.append(interesString);
        return builder;
    }

}
