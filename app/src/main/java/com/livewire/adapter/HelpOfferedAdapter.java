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
import android.widget.RatingBar;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.HelpOfferedResponce;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 10/1/18.
 */

public class HelpOfferedAdapter extends RecyclerView.Adapter<HelpOfferedAdapter.MyViewHolder> {
    private static final String TAG = HelpOfferedAdapter.class.getName();
    private Context mContext;
    private  List<HelpOfferedResponce.DataBean> dataBeanList;

    public HelpOfferedAdapter(Context mContext, List<HelpOfferedResponce.DataBean> data) {
        this.mContext = mContext ;
        this.dataBeanList= data;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_offered_cell, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (dataBeanList.size() != 0){
            HelpOfferedResponce.DataBean dataBean = dataBeanList.get(position);
            holder.tvCategory.setText(dataBean.getParentCategoryName());
            holder.tvSubcategory.setText(dataBean.getSubCategoryName());
            holder.tvBudget.setText("$ "+dataBean.getJob_budget());
            holder.tvName.setText(dataBean.getName());
            holder.tvDistance.setText(dataBean.getDistance_in_km()+" Km away");
           // holder.tvDate.setText(dataBean.getJob_start_date());
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).fit().into(holder.ivProfileImg);

            //********"2018-07-04" date format converted into "04 july 2018"***********//
            DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            String start= dataBean.getJob_start_date();

            try {
                Date newStartDate;
                newStartDate= sd.parse(start);
                sd = new SimpleDateFormat("dd MMMM yyyy");
                start = sd.format(newStartDate);

                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString userName = new SpannableString(start.substring(0,2));
                userName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, 2, 0);
                userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
                builder.append(userName);
                SpannableString interesString = new SpannableString(start.substring(3));
//                interesString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 3, start.length(), 0);

                builder.append(interesString);
                holder.tvDate.setText(builder);

               // holder.tvDate.setText(start);
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }

        }

    }


    @Override
    public int getItemCount() {
        return dataBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvTime;
        private View viewId;
        private TextView tvCategory;
        private TextView tvSubcategory;
        private TextView tvBudget;
        private TextView tvNoJobPost;
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private TextView tvDistance;
        private Button btnSendRequest;
        private TextView tvMoreInfo;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_date);
            tvTime = view.findViewById(R.id.tv_time);
            viewId = view.findViewById(R.id.view_id);
            tvCategory = view.findViewById(R.id.tv_category);
            tvSubcategory = view.findViewById(R.id.tv_subcategory);
            tvBudget = view.findViewById(R.id.tv_budget);

            ivProfileImg = view.findViewById(R.id.iv_profile_img);
            tvName = view.findViewById(R.id.tv_name);
            ratingBar = view.findViewById(R.id.rating_bar);
            tvDistance = view.findViewById(R.id.tv_distance);
            btnSendRequest = view.findViewById(R.id.btn_send_request);
            tvMoreInfo = view.findViewById(R.id.tv_more_info);
        }
    }
/*
    private void colorChangeText(){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString userName = new SpannableString();
        userName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, 2, 0);
        userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(userName);
        SpannableString interesString = new SpannableString(" send offer on your car.");
        builder.append(interesString);
        holder.userName.setText(builder);
        //holder.time.setText(Constant.getDayDifference(interestedListResponce.getUpd(), currentTime));
    }*/
}


