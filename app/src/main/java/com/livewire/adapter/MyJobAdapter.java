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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.MyjobResponceClient;
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

    public MyJobAdapter(Context mCon, List<MyjobResponceClient.DataBean> myJobList) {
        this.mContext = mCon;
        this.myJobList = myJobList;
    }

    @Override
    public int getItemViewType(int position) {
        if (myJobList.size() != 0) {
            MyjobResponceClient.DataBean dataBean = myJobList.get(position);
            int request = Integer.parseInt(dataBean.getTotal_request());
            if (request > 0) {
                return JOBCELL1;
            } else if (request == 0) {
                return JOBCELL2;
            }/*else if (position >8 && position <10){
            return  JOBCELL3;

        }*/
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

    private void setDataJobCell3(MyViewHolderJob3 noImageHolder3, int position) {

    }

    private void setDataJobCell2(MyViewHolderJob2 holder, int position) {
        if (myJobList.size() != 0) {
            MyjobResponceClient.DataBean dataBean = myJobList.get(position);
            holder.tvCategory.setText(dataBean.getParent_category());
            holder.tvSubcategory.setText(dataBean.getSub_category());
            holder.tvBudget.setText("$ " + dataBean.getJob_budget());
            //  holder.tvName.setText(dataBean.getName());
            // holder.tvDistance.setText(dataBean.getDistance_in_km() + " Km away");
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

    private void setDataJobCell1(MyViewHolderJob1 holder, int position) {
        if (myJobList.size() != 0) {
            MyjobResponceClient.DataBean dataBean = myJobList.get(position);
            holder.tvCategory.setText(dataBean.getParent_category());
            holder.tvSubcategory.setText(dataBean.getSub_category());
            holder.tvBudget.setText("$ " + dataBean.getJob_budget());
            //  holder.tvName.setText(dataBean.getName());
            // holder.tvDistance.setText(dataBean.getDistance_in_km() + " Km away");
            holder.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentTime()));

            //Picasso.with(holder.fl_image.getContext()).load(dataBean.getRequestedUserData().get(0).getProfileImage()).fit().into(holder.fl_image);

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
           /* LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           View layout = vi.inflate(R.layout.multiple_image_cell, null);
         //   mfView = (SingleFingerView) v1.findViewById(R.id.draw);
            CircleImageView circleImageView = layout.findViewById(R.id.iv_profile);
            circleImageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.livelogo));*/
     /*      CircleImageView circleImageView=new CircleImageView(mContext);
           circleImageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.livelogo));
           holder.fl_image.removeAllViews();
            holder.fl_image.addView(circleImageView);
            holder.fl_image.addView(circleImageView);
            holder.fl_image.addView(circleImageView);
            holder.fl_image.addView(circleImageView);*/
          /*  for (int i =0 ; i<dataBean.getRequestedUserData().size();i++){
            CircleImageView circleImageView = layout.findViewById(R.id.iv_profile);
            RelativeLayout rl_imageview = layout.findViewById(R.id.rl_imageview);
              //  Picasso.with(circleImageView.getContext()).load(dataBean.getRequestedUserData().get(i).getProfileImage()).fit().into(circleImageView);

                holder.fl_image.addView(circleImageView);
                }*/

        }


/*
// Parent layout
            FrameLayout parentLayout = (FrameLayout) findViewById(R.id.multiple_image_cell);

// Layout inflater
            LayoutInflater layoutInflater = getLayoutInflater();
            View view;

            for (int i = 1; i < 101; i++) {
                // Add the text layout to the parent layout
                view = layoutInflater.inflate(R.layout.text_layout, parentLayout, false);

                // In order to get the view we have to use the new view with text_layout in it
                TextView textView = (TextView) view.findViewById(R.id.text);
                textView.setText("Row " + i);

                // Add the text view to the parent layout
                parentLayout.addView(textView);
            }
*/


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
            tvBudget = (TextView) itemView.findViewById(R.id.tv_budget);
            view1Id = (View) itemView.findViewById(R.id.view1_id);
            fl_image = itemView.findViewById(R.id.iv_profile_img);
            tvRequested = (TextView) itemView.findViewById(R.id.tv_requested);
            llMoreInfo = (LinearLayout) itemView.findViewById(R.id.ll_more_info);
            tvMoreInfo = (TextView) itemView.findViewById(R.id.tv_more_info);

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
        private View view1Id;
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
            tvDate = view.findViewById(R.id.tv_date);
            tvTime = view.findViewById(R.id.tv_time);
            viewId = view.findViewById(R.id.view_id);
            tvCategory = view.findViewById(R.id.tv_category);
            tvSubcategory = view.findViewById(R.id.tv_subcategory);
            budget = view.findViewById(R.id.budget);
            tvBudget = view.findViewById(R.id.tv_budget);
            view1Id = view.findViewById(R.id.view1_id);
            prfileLayout = view.findViewById(R.id.prfile_layout);
            ivProfileImg = view.findViewById(R.id.iv_profile_img);
            tvName = view.findViewById(R.id.tv_name);
            ratingBar = view.findViewById(R.id.rating_bar);
            tvDistance = view.findViewById(R.id.tv_distance);
            tvJobConfirm = view.findViewById(R.id.tv_job_confirm);
            requestPending = view.findViewById(R.id.request_pending);
            llMoreInfo = view.findViewById(R.id.ll_more_info);
            tvMoreInfo = view.findViewById(R.id.tv_more_info);
        }
    }

    private class MyViewHolderJob3 extends RecyclerView.ViewHolder {
        public MyViewHolderJob3(View v3) {
            super(v3);
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

        // Apply the updated layout parameters to TextView
        showTime.setLayoutParams(lp);
        // final LinearLayout openCal = (LinearLayout) v.findViewById(R.id.open_cal);
     /*   openCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeField(showTime);
            }
        });*/
     /*   MonthlyOrderEntryBean newOrderEntryBean = new MonthlyOrderEntryBean();
        MonthlyOrderEntryBean.TimeTextView timeTextView = newOrderEntryBean.new TimeTextView();
        timeTextView.setOrderTimeTextView(showTime);
        timeTextViewArrayList.add(timeTextView);
        orderEntryBeanArrayList.get(linearPos).setTimeTextViews(timeTextViewArrayList);*/
        linearLayout.addView(v);
    }
}



