package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.RequestResponceClient;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 10/16/18.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    private Context mContext;
    private List<RequestResponceClient.DataBean> requestList;

    public RequestAdapter(Context mContext, List<RequestResponceClient.DataBean> requestList) {
        this.mContext = mContext;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_cell,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (requestList.size() != 0){
            RequestResponceClient.DataBean dataBean = requestList.get(position);

            holder.tvName.setText(dataBean.getName());
            holder.tvDistance.setText(dataBean.getDistance_in_km()+" Km away");
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).fit().into(holder.ivProfileImg);
        }
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private LinearLayout llKmAway;
        private TextView tvDistance;
        private Button btnIgnore;
        private Button btnAccept;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivProfileImg = (CircleImageView) itemView.findViewById(R.id.iv_profile_img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            llKmAway = (LinearLayout) itemView.findViewById(R.id.ll_km_away);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            btnIgnore = (Button) itemView.findViewById(R.id.btn_ignore);
            btnAccept = (Button) itemView.findViewById(R.id.btn_accept);
        }
    }
}
