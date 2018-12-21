package com.livewire.adapter;

import android.content.Context;
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
import com.livewire.responce.NotificationResponce;
import com.livewire.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 11/30/18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private List<NotificationResponce.DataBean> reviewList;
    private Context mContext;
    private String currentTime;
    private NotificationOnItemClickListener listener;

    public NotificationAdapter(Context mContext, List<NotificationResponce.DataBean> reviewList, NotificationOnItemClickListener listener) {
        this.mContext = mContext;
        this.reviewList = reviewList;
        this.listener = listener;
    }

    public void getCurrentTime(String currentTime) {
        this.currentTime = currentTime;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_cell, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        if (reviewList.size() != 0) {
            NotificationResponce.DataBean dataBean = reviewList.get(i);
            holder.getBinding().setVariable(BR.dataBean, dataBean);
            holder.getBinding().executePendingBindings();
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).fit().into(holder.ivProfileImg);
            holder.tvName.setText(dataBean.getName());
            holder.tvDescription.setText(" " + dataBean.getNotification_message().getBody());
            holder.tvDate.setText(Constant.getDayDifference(dataBean.getCrd(), currentTime));
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding viewDataBinding;
        private CircleImageView ivProfileImg;
        private TextView tvDate;
        private TextView tvName;
        private TextView tvDescription;
        private RelativeLayout notificationCell;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewDataBinding = DataBindingUtil.bind(itemView);
            ivProfileImg = itemView.findViewById(R.id.iv_profile_img);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            notificationCell = itemView.findViewById(R.id.notification_cell);
            notificationCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                listener.notificationOnClick(reviewList.get(getAdapterPosition()));
                }
            });

        }

        public ViewDataBinding getBinding() {
            return viewDataBinding;
        }
    }

    public interface NotificationOnItemClickListener {
        void notificationOnClick(NotificationResponce.DataBean dataBean);
    }
}

