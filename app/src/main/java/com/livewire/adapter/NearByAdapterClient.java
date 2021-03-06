package com.livewire.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.NearByResponce;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 2/27/19.
 */

public class NearByAdapterClient extends RecyclerView.Adapter<NearByAdapterClient.MyViewHolder> {
    private Context mContext;
    private ItemonClickListener listener;
    private List<NearByResponce.DataBean> nearByList;

    public NearByAdapterClient(Context mContext, List<NearByResponce.DataBean> nearByList, ItemonClickListener listener) {
        this.mContext = mContext;
        this.nearByList = nearByList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.near_by_cell, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        if (nearByList.size() > 0) {
            final NearByResponce.DataBean dataBean = nearByList.get(i);
            holder.tvName.setText(dataBean.getName());
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
                    .into(holder.ivProfileImg);
            assert dataBean.getDistance_in_km() != null;
            holder.tvDistance.setText(dataBean.getDistance_in_km() + " Km");
            float rating =  dataBean.getRating().isEmpty() ? 0:Float.parseFloat(dataBean.getRating());
                holder.ratingBar.setRating(rating);


            holder.rlProfileDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickWorker(dataBean.getUserId());
                }
            });

            holder.ivChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChattingActivity.class);
                    intent.putExtra("otherUID", dataBean.getUserId());
                    intent.putExtra("titleName", dataBean.getName());
                    intent.putExtra("profilePic", dataBean.getProfileImage());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (nearByList.size()>0){
            return nearByList.size();
        }else return 0;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlProfileDetail;
        private CircleImageView ivProfileImg;
        private ImageView ivChat;
        private TextView tvName;
        private RatingBar ratingBar;
        private LinearLayout llkmAway;
        private TextView tvDistance;

        public MyViewHolder(@NonNull View view) {
            super(view);
            rlProfileDetail = (RelativeLayout) view.findViewById(R.id.rl_profile);
            ivProfileImg = (CircleImageView) view.findViewById(R.id.iv_profile_img);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            llkmAway = (LinearLayout) view.findViewById(R.id.llkm_away);
            tvDistance = (TextView) view.findViewById(R.id.tv_distance);
            ivChat = view.findViewById(R.id.iv_chat);
        }
    }

    public interface ItemonClickListener {
        void onItemClickWorker(String workerId);
    }
}

