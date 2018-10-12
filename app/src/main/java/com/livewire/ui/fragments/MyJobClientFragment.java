package com.livewire.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.MyJobAdapter;
import com.livewire.responce.MyjobResponceClient;
import com.livewire.ui.activity.NearYouClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;


public class MyJobClientFragment extends Fragment {
    private Context mContext;
   private ProgressDialog progressDialog;
   private FrameLayout mainLayout;
    private MyJobAdapter adapter;
    private List<MyjobResponceClient.DataBean> myJobList;

    public MyJobClientFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_job_client, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        Button btn = view.findViewById(R.id.btn_near_you);

        myJobList = new ArrayList<>();
        mainLayout = view.findViewById(R.id.main_layout);
        RecyclerView rvMyjob = view.findViewById(R.id.rv_myjob);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvMyjob.setLayoutManager(layoutManager);
         adapter = new MyJobAdapter(mContext,myJobList);
        rvMyjob.setAdapter(adapter);

        myJobListApi();
                btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NearYouClientActivity.class);
                startActivity(intent);

            }
        });

    }


    private void myJobListApi() {// help offer api calling
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/getMyJobList")
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                          //  tvNoJobPost.setVisibility(View.GONE);
                            myJobList.clear();
                            MyjobResponceClient myjobResponceClient = new Gson().fromJson(String.valueOf(response), MyjobResponceClient.class);
                            myJobList.addAll(myjobResponceClient.getData());
                            adapter.notifyDataSetChanged();

                        } else {
                            myJobList.clear();
                            adapter.notifyDataSetChanged();
                            if (myJobList.size() == 0) {
                               // tvNoJobPost.setVisibility(View.VISIBLE);
                            }
                            Constant.snackBar(mainLayout, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    progressDialog.dismiss();
                }
            });
        }
    }

}
