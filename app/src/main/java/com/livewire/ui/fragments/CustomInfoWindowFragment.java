package com.livewire.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.livewire.R;
import com.livewire.databinding.FragmentCustomInfoWindowBinding;
import com.livewire.responce.NearByResponce;
import com.livewire.ui.activity.WorkerProfileDetailClientActivity;
import com.squareup.picasso.Picasso;

import java.io.Serializable;


public class CustomInfoWindowFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FragmentCustomInfoWindowBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private NearByResponce.DataBean userData;

    public CustomInfoWindowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    public static CustomInfoWindowFragment newInstance(NearByResponce.DataBean param1) {
        CustomInfoWindowFragment fragment = new CustomInfoWindowFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userData = (NearByResponce.DataBean) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater,R.layout.fragment_custom_info_window, container, false);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WorkerProfileDetailClientActivity.class);
                intent.putExtra("NearBy", "NearBy");
                intent.putExtra("UserIdKey", userData.getUserId());
                mContext.startActivity(intent);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvName.setText(userData.getName());
        binding.tvDistance.setText(userData.getDistance_in_km()+" km");
        if (!userData.getRating().isEmpty())
        binding.ratingBar.setRating(Float.parseFloat(userData.getRating()));

        Picasso.with(binding.ivProfileImg.getContext()).load(userData.getProfileImage()).error(R.drawable.ic_user)
                .placeholder(R.drawable.ic_user)
                .into(binding.ivProfileImg);
    }
}
