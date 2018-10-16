package com.livewire.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.ui.activity.UserSelectionActivity;
import com.livewire.utils.PreferenceConnector;


public class MyProfileClientFragment extends Fragment implements View.OnClickListener{
    private Context mContext;

    public MyProfileClientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBarIntialize(view);

        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }
    private void actionBarIntialize(View view) {
        View actionBar = view.findViewById(R.id.action_bar1);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);
        header.setText(R.string.settings);
        header.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        ImageView ivFilter = actionBar.findViewById(R.id.iv_filter);
        ivFilter.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                PreferenceConnector.clear(mContext);
                Intent intent = new Intent(mContext,UserSelectionActivity.class);
                startActivity(intent);
                getActivity().finish();
        }
    }
}
