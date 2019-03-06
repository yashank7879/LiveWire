package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.NearByResponce;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 2/27/19.
 */

public class NearByAdapterWorker extends RecyclerView.Adapter<NearByAdapterWorker.MyViewHolder> {
    private Context mContext;
    private ItemonClickListener listener;
    private List<NearByResponce.DataBean> nearByList;

    public NearByAdapterWorker(Context mContext, List<NearByResponce.DataBean> nearByList, ItemonClickListener listener) {
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
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage())
                    .into(holder.ivProfileImg);
            assert dataBean.getDistance_in_km() != null;
            holder.tvDistance.setText(dataBean.getDistance_in_km() + " Km away");
            if (dataBean.getRating() != null && !dataBean.getRating().isEmpty()) {
                holder.ratingBar.setRating(Float.parseFloat(dataBean.getRating()));
            }

            holder.rlProfileDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 listener.onItemClickClient(dataBean.getUserId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return nearByList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlProfileDetail;
        private CircleImageView ivProfileImg;
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
        }
    }

    public interface ItemonClickListener{
        void onItemClickClient(String clientId);
    }
}

