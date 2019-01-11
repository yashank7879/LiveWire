package com.livewire.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.livewire.BR;
import com.livewire.R;
import com.livewire.responce.CompleteJobResponce;
import com.livewire.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mindiii on 11/16/18.
 */

public class CompletedJobAdapter extends RecyclerView.Adapter<CompletedJobAdapter.MyViewHolder> {
    CompleteJobClientListener listener;
    private List<CompleteJobResponce.DataBean> completeJobList;
    private Context mContext;
    private String currentTime;

    public CompletedJobAdapter(Context mContext, CompleteJobClientListener listener, List<CompleteJobResponce.DataBean> completeJobList, String currentTime) {
        this.mContext = mContext;
        this.completeJobList = completeJobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.completed_job_cell, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        if (completeJobList.size() != 0) {
            CompleteJobResponce.DataBean dataBean = completeJobList.get(i);
            holder.getBinding().setVariable(BR.dataBean, dataBean);
            holder.getBinding().executePendingBindings();
            Picasso.with(holder.ivProfileImg.getContext())
                    .load(dataBean.getProfileImage()).fit()
                    .into(holder.ivProfileImg);
            holder.tvDate.setText(Constant.dateTextColorChange(mContext, Constant.DateFomatChange(dataBean.getJob_start_date())));
            holder.tvTime.setText(Constant.getDayDifference(dataBean.getUpd(), currentTime));
            if (!dataBean.getRating().isEmpty()){
                holder.ratingBar.setRating(Float.parseFloat(dataBean.getRating()));
            }

          /*  if ( dataBean.getRating() != null || !dataBean.getRating().isEmpty()) {
                holder.ratingBar.setRating(Float.parseFloat(dataBean.getRating()));
            }*/

            if (dataBean.getJob_type().equals("2")){
            float paidAmount  =(Float.parseFloat(dataBean.getJob_time_duration()) * Float.parseFloat(dataBean.getJob_offer())* Float.parseFloat(dataBean.getNumber_of_days()));
                holder.tv_offer_rate.setText("$ "+paidAmount);

            }else {
                holder.tv_offer_rate.setText("$ "+dataBean.getJob_budget());
            }
        }

    }

    @Override
    public int getItemCount() {
        return completeJobList.size();
    }

    public void currentTime1(String currentTime) {
        this.currentTime = currentTime;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;
        TextView tvTime;
        TextView tvDate;
        TextView tv_offer_rate;
        ImageView ivProfileImg;
        LinearLayout llMoreInfo;
        RatingBar ratingBar ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivProfileImg = itemView.findViewById(R.id.iv_profile_img);
            llMoreInfo = itemView.findViewById(R.id.ll_more_info);
            tv_offer_rate = itemView.findViewById(R.id.tv_offer_rate);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            llMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.singlejobcompleteMoreInfoListener(completeJobList.get(getAdapterPosition()).getJob_type(), completeJobList.get(getAdapterPosition()).getJobId());
                }
            });
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }

    public interface CompleteJobClientListener {
        void singlejobcompleteMoreInfoListener(String jobType, String jobId);
        // void onGoingjobcompleteMoreInfoListener(String jobType, String jobId);
    }
}
