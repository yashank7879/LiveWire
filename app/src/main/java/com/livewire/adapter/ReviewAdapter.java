package com.livewire.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.BR;
import com.livewire.R;
import com.livewire.responce.GetReviewResponce;
import com.livewire.ui.activity.WorkerProfileDetailClientActivity;
import com.livewire.utils.Constant;
import com.squareup.picasso.Picasso;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 11/30/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private List<GetReviewResponce.DataBean> reviewList;
    private Context mContext;

    public ReviewAdapter(Context mContext, List<GetReviewResponce.DataBean> reviewList) {
        this.mContext = mContext;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_cell, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        if (reviewList.size() != 0) {
            GetReviewResponce.DataBean dataBean = reviewList.get(i);
            holder.getBinding().setVariable(BR.dataBean, dataBean);
            holder.getBinding().executePendingBindings();
            holder.ratingBar.setRating(Float.parseFloat(dataBean.getRating()));
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).fit().into(holder.ivProfileImg);
            holder.tvDate.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentDateTime()));
      holder.itemCell.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(mContext, WorkerProfileDetailClientActivity.class);
              intent.putExtra("UserIdKey",""+dataBean.getUserId());
              mContext.startActivity(intent);
          }
      });

        }

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding viewDataBinding;
        private CircleImageView ivProfileImg;
        private RatingBar ratingBar;
        private TextView tvDate;
        private RelativeLayout itemCell;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewDataBinding = DataBindingUtil.bind(itemView);
            ivProfileImg = itemView.findViewById(R.id.iv_profile_img);
            ratingBar =  itemView.findViewById(R.id.rating_bar);
            tvDate =  itemView.findViewById(R.id.tv_date);
            itemCell =  itemView.findViewById(R.id.item_view);
        }

        public ViewDataBinding getBinding() {
            return viewDataBinding;
        }
    }
}

