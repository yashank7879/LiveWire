package com.livewire.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

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


import com.livewire.BR;
import com.livewire.R;
import com.livewire.responce.HelpOfferedResponce;
import com.livewire.utils.Constant;
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
    private  HelpOfferItemListener listener;
    private RelativeLayout manLayout;

    public HelpOfferedAdapter(Context mContext, List<HelpOfferedResponce.DataBean> data, HelpOfferItemListener listener, RelativeLayout mainLayout) {
        this.mContext = mContext ;
        this.dataBeanList= data;
        this.listener = listener;
        this.manLayout = mainLayout;
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
            holder.getBinding().setVariable(BR.dataBean,dataBean);
            holder.getBinding().executePendingBindings();

            holder.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(),dataBean.getCurrentDateTime()));
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).fit().into(holder.ivProfileImg);

            //********"2018-07-04" date format converted into "04 july 2018"***********//
            holder.tvDate.setText(Constant.dateTextColorChange(mContext,Constant.DateFomatChange(dataBean.getJob_start_date())));

          /*  DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            String start= dataBean.getJob_start_date();

            try {
                Date newStartDate;
                newStartDate= sd.parse(start);
                sd = new SimpleDateFormat("dd MMM yyyy");
                start = sd.format(newStartDate);

                holder.tvDate.setText(dateTextColorChange(start));
                // holder.tvDate.setText(start);
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }*/

           if (dataBean.getJob_confirmed().equals("0")){ // pending request
               holder.btnSendRequest.setBackground(null);
               holder.btnSendRequest.setText(R.string.pending_request);
               holder.btnSendRequest.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
               holder.btnSendRequest.setClickable(false);
   /*        }else if (dataBean.getJob_confirmed().equals("1")){// accepted
               holder.btnSendRequest.setClickable(false);
           }else if (dataBean.getJob_confirmed().equals("2")){// job not accepted
            holder.btnSendRequest.setClickable(false);*/
            }else if (dataBean.getJob_confirmed().equals("3")){// job not send
               holder.btnSendRequest.setBackground(mContext.getResources().getDrawable(R.drawable.button_green_bg));
               holder.btnSendRequest.setText(R.string.send_request);
               holder.btnSendRequest.setTextColor(ContextCompat.getColor(mContext,R.color.colorWhite));
           }
        }

    }


    @Override
    public int getItemCount() {
        return dataBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;
        private TextView tvDate;
        private TextView tvTime;

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
        private LinearLayout llMoreInfo;
        public MyViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);

            tvDate = view.findViewById(R.id.tv_date);
            tvTime = view.findViewById(R.id.tv_time);
            //viewId = view.findViewById(R.id.view_id);
            tvCategory = view.findViewById(R.id.tv_category);
            tvSubcategory = view.findViewById(R.id.tv_subcategory);
            tvBudget = view.findViewById(R.id.tv_offer_rate);
            llMoreInfo = view.findViewById(R.id.ll_more_info);

            ivProfileImg = view.findViewById(R.id.iv_profile_img);
            tvName = view.findViewById(R.id.tv_name);
            ratingBar = view.findViewById(R.id.rating_bar);
            tvDistance = view.findViewById(R.id.tv_distance);
            btnSendRequest = view.findViewById(R.id.btn_send_request);
            tvMoreInfo = view.findViewById(R.id.tv_more_info);

            llMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.helpOfferItemOnClick(dataBeanList.get(getAdapterPosition()),mContext.getString(R.string.moreinfo),getAdapterPosition());
                }
            });

            btnSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.helpOfferItemOnClick(dataBeanList.get(getAdapterPosition()), mContext.getString(R.string.sendrequest),getAdapterPosition());
                    if (Constant.isNetworkAvailable(mContext,manLayout)) {
                        btnSendRequest.setBackground(null);
                        btnSendRequest.setText(R.string.pending_request);
                        btnSendRequest.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
                        //   btnSendRequest.setVisibility(View.GONE);
                        btnSendRequest.setClickable(false);
                    }
                }
            });

        }
        public ViewDataBinding getBinding() {

            return binding;

        }
    }
    private SpannableStringBuilder dateTextColorChange(String start){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString userName = new SpannableString(start.substring(0,2)+" ");
        userName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, 2, 0);
        userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(userName);
        SpannableString interesString = new SpannableString(start.substring(3));
//                interesString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 3, start.length(), 0);
        builder.append(interesString);
        return  builder;
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

    public interface HelpOfferItemListener{
        void helpOfferItemOnClick(HelpOfferedResponce.DataBean dataBean,String helpoffer, int pos);
    }

}


