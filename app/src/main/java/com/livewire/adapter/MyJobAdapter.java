package com.livewire.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.MyjobResponceClient;
import com.livewire.ui.activity.RequestClientActivity;
import com.livewire.utils.Constant;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 10/12/18.
 */

public class MyJobAdapter extends RecyclerView.Adapter {
    private static final int JOBCELL1 = 1;
    private static final int JOBCELL2 = 2;
    private static final int JOBCELL3 = 3;
    private Context mContext;
    private List<MyjobResponceClient.DataBean> myJobList;
    private OnClickMoreInfoListener listener;

    public MyJobAdapter(Context mCon, List<MyjobResponceClient.DataBean> myJobList, OnClickMoreInfoListener listener) {
        this.mContext = mCon;
        this.myJobList = myJobList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (myJobList.size() != 0) {
            MyjobResponceClient.DataBean dataBean = myJobList.get(position);
            int jobType = Integer.parseInt(dataBean.getJob_type());
            int request = Integer.parseInt(dataBean.getTotal_request());
            if (jobType == 1 &&  request != 0) {
                return JOBCELL1;
            } else if (jobType == 2 ) {
                return JOBCELL3;
            }else if (jobType == 1){
            return  JOBCELL2;
        }
        }
        return 0;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case JOBCELL1:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_cell1, parent, false);
                return new MyViewHolderJob1(v);

            case JOBCELL2:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_cell2, parent, false);
                return new MyViewHolderJob2(v1);

            case JOBCELL3:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_cell3, parent, false);
                return new MyViewHolderJob3(v3);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case JOBCELL1:
                MyViewHolderJob1 noImageHolder1 = (MyViewHolderJob1) holder;
                setDataJobCell1(noImageHolder1, position);
                break;
            case JOBCELL2:
                MyViewHolderJob2 noImageHolder2 = (MyViewHolderJob2) holder;
                setDataJobCell2(noImageHolder2, position);
                break;
            case JOBCELL3:
                MyViewHolderJob3 noImageHolder3 = (MyViewHolderJob3) holder;
                setDataJobCell3(noImageHolder3, position);
                break;
            default:

        }
    }

    private void setDataJobCell3(MyViewHolderJob3 holder, int position) {
        if (myJobList.size() != 0) {
            MyjobResponceClient.DataBean dataBean = myJobList.get(position);
            holder.tvCategory.setText(dataBean.getParent_category());
            holder.tvSubcategory.setText(dataBean.getSub_category());
          //  holder.tvBudget.setText("$ " + dataBean.getJob_budget());

            if ( dataBean.getJob_offer()== null || dataBean.getJob_offer().equals("") ){
                holder.rlData.setVisibility(View.GONE);
                holder.rlRange.setVisibility(View.GONE);
                holder.rlRange.setVisibility(View.GONE);
                holder.rlOfferRange.setVisibility(View.GONE);
                holder.tvOfferRequest.setVisibility(View.VISIBLE);
            }else {
                holder.rlData.setVisibility(View.VISIBLE);
                holder.rlRange.setVisibility(View.VISIBLE);
                holder.rlRange.setVisibility(View.VISIBLE);
                holder.tvOfferRequest.setVisibility(View.GONE);
                holder.rlOfferRange.setVisibility(View.VISIBLE);
                holder.tvOfferPrice.setText("$ "+dataBean.getJob_offer());
                holder.tvName.setText(dataBean.getRequestedUserData().get(0).getName());
                holder.tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km()+" Km away");
                Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getRequestedUserData().get(0).getProfileImage()).fit().into(holder.ivProfileImg);

                if (dataBean.getRequestedUserData().get(0).getRequest_status().equals("0")){
                    holder.tvPendingRequest.setText("Request Pending");
                    holder.tvPendingRequest.setTextColor(ContextCompat.getColor(mContext,R.color.colorOrange));

                }else{
                    holder.tvPendingRequest.setText("Request Confirmed");
                    holder.tvPendingRequest.setTextColor(ContextCompat.getColor(mContext,R.color.colorGreen));
                }

                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString minprice = new SpannableString("$ " + dataBean.getRequestedUserData().get(0).getMin_rate());
                minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, minprice.length(), 0);
                //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
                builder.append(minprice);
                SpannableString toString = new SpannableString(" to ");
                builder.append(toString);


                SpannableString maxprice = new SpannableString("$ " + dataBean.getRequestedUserData().get(0).getMax_rate());
                maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
                //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
                builder.append(maxprice);

                holder.tvRangePrice.setText(builder);
            }

            holder.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentTime()));
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

    private void setDataJobCell2(MyViewHolderJob2 holder, int position) {
        if (myJobList.size() != 0) {
            MyjobResponceClient.DataBean dataBean = myJobList.get(position);

            if ( dataBean.getTotal_request().equals("0")) {
                holder.prfileLayout.setVisibility(View.GONE);
                holder.tvJobConfirm.setVisibility(View.GONE);
                holder.requestPending.setVisibility(View.VISIBLE);
            }else {
                holder.prfileLayout.setVisibility(View.VISIBLE);
                holder.tvJobConfirm.setVisibility(View.VISIBLE);
                holder.requestPending.setVisibility(View.GONE);
            }


            holder.tvCategory.setText(dataBean.getParent_category());
            holder.tvSubcategory.setText(dataBean.getSub_category());
            holder.tvBudget.setText("$ " + dataBean.getJob_budget());
            holder.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentTime()));

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

    private void setDataJobCell1(MyViewHolderJob1 holder, int position) {
        if (myJobList.size() != 0) {
            MyjobResponceClient.DataBean dataBean = myJobList.get(position);

            holder.tvCategory.setText(dataBean.getParent_category());
            holder.tvSubcategory.setText(dataBean.getSub_category());
            holder.tvBudget.setText("$ " + dataBean.getJob_budget());
            holder.tvRequested.setText(""+dataBean.getTotal_request()+" Members Requested");
            holder.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentTime()));


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
            int leftMargin = 0;
            for (int i = 0; i < dataBean.getRequestedUserData().size(); i++) {
                if (i!=0) {
                    leftMargin = leftMargin + 35;
                }
                addhorizontalTimeView(holder.fl_image, dataBean.getRequestedUserData().get(i).getProfileImage(), leftMargin);
            }

        }

    }


    @Override
    public int getItemCount() {
        return myJobList.size();
    }

    public class MyViewHolderJob1 extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvTime;
        View viewId;
        TextView tvCategory;
        TextView tvSubcategory;
        TextView budget;
        TextView tvBudget;
        View view1Id;
        FrameLayout fl_image;
        TextView tvRequested;
        LinearLayout llMoreInfo;
        TextView tvMoreInfo;

        public MyViewHolderJob1(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            viewId = (View) itemView.findViewById(R.id.view_id);
            tvCategory = (TextView) itemView.findViewById(R.id.tv_category);
            tvSubcategory = (TextView) itemView.findViewById(R.id.tv_subcategory);
            budget = (TextView) itemView.findViewById(R.id.budget);
            tvBudget = (TextView) itemView.findViewById(R.id.tv_offer_rate);
            view1Id = (View) itemView.findViewById(R.id.view1_id);
            fl_image = itemView.findViewById(R.id.iv_profile_img);
            tvRequested = (TextView) itemView.findViewById(R.id.tv_requested);
            llMoreInfo = (LinearLayout) itemView.findViewById(R.id.ll_more_info);
            tvMoreInfo = (TextView) itemView.findViewById(R.id.tv_more_info);
            fl_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RequestClientActivity.class);
                   // intent.putExtra("","");
                    mContext.startActivity(intent);
                }
            });
            llMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.moreInfoOnClickClient(myJobList.get(getAdapterPosition()));
                }
            });

        }
    }

    private class MyViewHolderJob2 extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvTime;
        private View viewId;
        private TextView tvCategory;
        private TextView tvSubcategory;
        private TextView budget;
        private TextView tvBudget;
        private RelativeLayout prfileLayout;
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private TextView tvDistance;
        private TextView tvJobConfirm;
        private TextView requestPending;
        private LinearLayout llMoreInfo;
        private TextView tvMoreInfo;

        public MyViewHolderJob2(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            viewId = (View) view.findViewById(R.id.view_id);
            tvCategory = (TextView) view.findViewById(R.id.tv_category);
            tvSubcategory = (TextView) view.findViewById(R.id.tv_subcategory);
            budget = (TextView) view.findViewById(R.id.budget);
            tvBudget = (TextView) view.findViewById(R.id.tv_offer_rate);
            prfileLayout = (RelativeLayout) view.findViewById(R.id.prfile_layout);
            ivProfileImg = (CircleImageView) view.findViewById(R.id.iv_profile_img);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            tvDistance = (TextView) view.findViewById(R.id.tv_distance);
            tvJobConfirm = (TextView) view.findViewById(R.id.tv_job_confirm);
            requestPending = (TextView) view.findViewById(R.id.request_pending);
            llMoreInfo = (LinearLayout) view.findViewById(R.id.ll_more_info);
            tvMoreInfo = (TextView) view.findViewById(R.id.tv_more_info);
            llMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.moreInfoOnClickClient(myJobList.get(getAdapterPosition()));
                }
            });
        }
    }

    private class MyViewHolderJob3 extends RecyclerView.ViewHolder {
        private RelativeLayout rlOfferRange;
        private TextView tvStartFrom;
        private TextView tvDate;
        private TextView tvTime;
        private View viewId;
        private TextView tvCategory;
        private TextView tvSubcategory;
        private TextView offeredPrice;
        private TextView tvOfferPrice;
        private TextView hr;
        private View view1Id;
        private RelativeLayout rlData;
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private LinearLayout llKmAway;
        private TextView tvDistance;
        private TextView tvPendingRequest;
        private RelativeLayout rlRange;
        private TextView btnSendRequest;
        private TextView tvRangePrice;
        private LinearLayout llMoreInfo;
        private TextView tvMoreInfo;
        private TextView tvOfferRequest;
        public MyViewHolderJob3(View view3) {
            super(view3);
            tvStartFrom = (TextView) view3.findViewById(R.id.tv_start_from);
            tvDate = (TextView) view3.findViewById(R.id.tv_date);
            tvTime = (TextView) view3.findViewById(R.id.tv_time);
            viewId = (View) view3.findViewById(R.id.view_id);
            tvCategory = (TextView) view3.findViewById(R.id.tv_category);
            tvSubcategory = (TextView) view3.findViewById(R.id.tv_subcategory);
            offeredPrice = (TextView) view3.findViewById(R.id.offered_price);
            tvOfferPrice = (TextView) view3.findViewById(R.id.tv_offer_price);
            hr = (TextView) view3.findViewById(R.id.hr);
            view1Id = (View) view3.findViewById(R.id.view1_id);
            rlData = (RelativeLayout) view3.findViewById(R.id.rl_data);
            ivProfileImg = (CircleImageView) view3.findViewById(R.id.iv_profile_img);
            tvName = (TextView) view3.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) view3.findViewById(R.id.rating_bar);
            llKmAway = (LinearLayout) view3.findViewById(R.id.ll_km_away);
            tvDistance = (TextView) view3.findViewById(R.id.tv_distance);
            tvPendingRequest = (TextView) view3.findViewById(R.id.tv_pending_request);
            rlRange = (RelativeLayout) view3.findViewById(R.id.rl_range);
            btnSendRequest = (TextView) view3.findViewById(R.id.btn_send_request);
            tvRangePrice = (TextView) view3.findViewById(R.id.tv_range_price);
            llMoreInfo = (LinearLayout) view3.findViewById(R.id.ll_more_info);
            tvMoreInfo = (TextView) view3.findViewById(R.id.tv_more_info);
            tvOfferRequest = (TextView) view3.findViewById(R.id.tv_offer_request);
            rlOfferRange = view3.findViewById(R.id.rl_offer_range);
            llMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.moreInfoOnClickClient(myJobList.get(getAdapterPosition()));
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

    void addhorizontalTimeView(FrameLayout linearLayout, String profileImage, int leftMargin) {
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View v = layoutInflater.inflate(R.layout.multiple_image_cell, linearLayout, false);

        final ImageView showTime = v.findViewById(R.id.iv_profile);

        Picasso.with(showTime.getContext()).load(profileImage).fit().into(showTime);

        // Get the TextView current LayoutParams
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) showTime.getLayoutParams();

        // Set TextView layout margin 25 pixels to all side
        // Left Top Right Bottom Margin
        lp.setMargins(leftMargin, 0, 0, 0);
        showTime.setLayoutParams(lp);

        linearLayout.addView(v);
    }

    public interface OnClickMoreInfoListener {
        void moreInfoOnClickClient(MyjobResponceClient.DataBean dataBean);
    }
}





