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
import android.widget.TextView;

import com.livewire.BR;
import com.livewire.R;
import com.livewire.responce.GetReviewResponce;
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
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_cell,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        if (reviewList.size() != 0){
            GetReviewResponce.DataBean dataBean = reviewList.get(i);

            holder.getBinding().setVariable(BR.dataBean,dataBean);
            holder.getBinding().executePendingBindings();
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).fit().into(holder.ivProfileImg);
        }

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding viewDataBinding;
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private TextView tvDescription;
        private TextView tvDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewDataBinding = DataBindingUtil.bind(itemView);
            ivProfileImg = (CircleImageView) itemView.findViewById(R.id.iv_profile_img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }

        public ViewDataBinding getBinding() {
            return viewDataBinding;
        }
    }
}

/*protected class ViewHolder {
    private CircleImageView ivProfileImg;
    private TextView tvName;
    private RatingBar ratingBar;
    private TextView tvDescription;
    private TextView tvDate;

    public ViewHolder(View view) {
        ivProfileImg = (CircleImageView) view.findViewById(R.id.iv_profile_img);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        tvDescription = (TextView) view.findViewById(R.id.tv_description);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
    }
}
}*/
