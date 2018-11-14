package com.livewire.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.OnGoingWorkerResponce;
import com.livewire.utils.Constant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 10/22/18.
 */

public class OngoingAdapter extends RecyclerView.Adapter<OngoingAdapter.MyViewHolder> {
    private OnGoingItemOnClick listener;
    private Context mContext;
    private List<OnGoingWorkerResponce.DataBean> ongoingList;

    public OngoingAdapter(Context mContext, List<OnGoingWorkerResponce.DataBean> ongoingList, OnGoingItemOnClick listener) {
        this.mContext = mContext;
        this.ongoingList = ongoingList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.on_going_cell, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (ongoingList.size() != 0) {
            OnGoingWorkerResponce.DataBean dataBean = ongoingList.get(position);
            holder.tvCategory.setText(dataBean.getParentCategoryName());
            holder.tvSubcategory.setText(dataBean.getSubCategoryName());
            holder.tvOfferPrice.setText(dataBean.getJob_offer_rate());
            holder.tvName.setText(dataBean.getName());
            holder.tvDistance.setText(dataBean.getDistance_in_km() + " Km away");
            holder.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentDateTime()));

            //********"2018-07-04" date format converted into "04 july 2018"***********//
            DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            String start = dataBean.getJob_start_date();

            try {
                Date newStartDate;
                newStartDate = sd.parse(start);
                sd = new SimpleDateFormat("dd MMM yyyy");
                start = sd.format(newStartDate);

                holder.tvDate.setText(dateTextColorChange(start));
                // holder.tvDate.setText(start);
            } catch (ParseException e) {
                Log.e("k", e.getMessage());
            }
        }

    }


    @Override
    public int getItemCount() {
        return ongoingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStartFrom;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvCategory;
        private TextView tvSubcategory;
        private RelativeLayout rlOfferRange;
        private TextView offeredPrice;
        private TextView tvOfferPrice;
        private TextView hr;
        private RelativeLayout rlDat;
        private RelativeLayout rlData;
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private LinearLayout llKmAway;
        private TextView tvDistance;
        private RelativeLayout rlRange;
        private LinearLayout llMoreInfo;
        private TextView tvMoreInfo;
        private Button btnAccept, btnIgnore;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvStartFrom = (TextView) itemView.findViewById(R.id.tv_start_from);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvCategory = (TextView) itemView.findViewById(R.id.tv_category);
            tvSubcategory = (TextView) itemView.findViewById(R.id.tv_subcategory);
            rlOfferRange = (RelativeLayout) itemView.findViewById(R.id.rl_offer_range);
            offeredPrice = (TextView) itemView.findViewById(R.id.offered_price);
            tvOfferPrice = (TextView) itemView.findViewById(R.id.tv_offer_price);
            hr = (TextView) itemView.findViewById(R.id.hr);
            rlDat = (RelativeLayout) itemView.findViewById(R.id.rl_dat);
            rlData = (RelativeLayout) itemView.findViewById(R.id.rl_data);
            ivProfileImg = (CircleImageView) itemView.findViewById(R.id.iv_profile_img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            llKmAway = (LinearLayout) itemView.findViewById(R.id.ll_km_away);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            rlRange = (RelativeLayout) itemView.findViewById(R.id.rl_range);
            btnIgnore = itemView.findViewById(R.id.btn_ignore);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            llMoreInfo = (LinearLayout) itemView.findViewById(R.id.ll_more_info);
            tvMoreInfo = (TextView) itemView.findViewById(R.id.tv_more_info);
            llMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListner(ongoingList.get(getAdapterPosition()), "MoreInfo");
                }
            });
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListner(ongoingList.get(getAdapterPosition()), "Accept");
                    ongoingList.remove(getAdapterPosition());
                }
            });

            btnIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListner(ongoingList.get(getAdapterPosition()), "Reject");
                    ongoingList.remove(getAdapterPosition());
                }
            });

        }
    }

    private SpannableStringBuilder dateTextColorChange(String start) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString userName = new SpannableString(start.substring(0, 2) + " ");
        userName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, 2, 0);
        userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(userName);
        SpannableString interesString = new SpannableString(start.substring(3));
//                interesString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 3, start.length(), 0);
        builder.append(interesString);
        return builder;
    }

    public interface OnGoingItemOnClick {
        void onItemClickListner(OnGoingWorkerResponce.DataBean dataBean, String moreInfo);
    }
}

