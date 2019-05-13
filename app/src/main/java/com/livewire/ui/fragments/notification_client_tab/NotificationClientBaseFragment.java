package com.livewire.ui.fragments.notification_client_tab;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.adapter.NotificationPagerAdpter;
import com.livewire.databinding.FragmentNotificationClientBaseBinding;
import com.livewire.ui.fragments.NotificationWorkerClientFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationClientBaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationClientBaseFragment extends Fragment {
    FragmentNotificationClientBaseBinding binding;
private Context mContext;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public NotificationClientBaseFragment() {
        // Required empty public constructor
    }

    public static NotificationClientBaseFragment newInstance(String param1, String param2) {
        NotificationClientBaseFragment fragment = new NotificationClientBaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notification_client_base, container, false);
    return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> headerList = new ArrayList<>();
        fragmentList.add(new NotificationClientFragment());
        fragmentList.add(new NotificationWorkerClientFragment());
        headerList.add("Hirer");
        headerList.add("Worker");
        NotificationPagerAdpter adapter = new NotificationPagerAdpter(getChildFragmentManager(),getActivity(),fragmentList,headerList);

        binding.viewpager.setAdapter(adapter);
        binding.slidingTabs.setupWithViewPager(binding.viewpager);
        final Typeface tf = ResourcesCompat.getFont(mContext,R.font.poppins_medium);
        final Typeface rl = ResourcesCompat.getFont(mContext,R.font.poppins_regular);

        //""""" Zero position selected so change fontFamily """""""""""//
        for (int i=0;i<binding.slidingTabs.getTabCount();i++){
            LinearLayout tabLayout1 = (LinearLayout) ((ViewGroup) binding.slidingTabs.getChildAt(0)).getChildAt(i);
            TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
            tabTextView.setAllCaps(false);
            tabTextView.setTypeface(tf);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
