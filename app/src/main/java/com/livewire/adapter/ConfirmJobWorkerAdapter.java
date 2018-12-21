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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 10/1/18.
 */

public class ConfirmJobWorkerAdapter extends RecyclerView.Adapter<ConfirmJobWorkerAdapter.MyViewHolder> {

    private static final String TAG = ConfirmJobWorkerAdapter.class.getName();
    private Context mContext;
    private List<HelpOfferedResponce.DataBean> dataBeanList;
    private RelativeLayout manLayout;
    private String currentDateTime = "";
    private ConfirmJobListener listener;

    public ConfirmJobWorkerAdapter(Context mContext, List<HelpOfferedResponce.DataBean> data, RelativeLayout mainLayout, ConfirmJobListener listener) {
        this.mContext = mContext;
        this.dataBeanList = data;
        this.manLayout = mainLayout;
        this.listener = listener;
    }

    public void getCurrentTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_job_cell, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (dataBeanList.size() != 0) {
            HelpOfferedResponce.DataBean dataBean = dataBeanList.get(position);
            holder.getBinding().setVariable(BR.dataBean, dataBean);
            holder.getBinding().executePendingBindings();

            holder.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), currentDateTime));
            Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).fit().into(holder.ivProfileImg);
            //********"2018-07-04" date format converted into "04 july 2018"***********//
            holder.tvDate.setText(Constant.dateTextColorChange(mContext, Constant.DateFomatChange(dataBean.getJob_start_date())));
            if (!dataBean.getRating().isEmpty()) {
                holder.ratingBar.setRating(Float.parseFloat(dataBean.getRating()));
            }
            // @{!dataBean.job_budget.equals("") ? "$ "+dataBean.job_budget : "$ "+ dataBean.job_offer, default= "$ 0"}
            if (!dataBean.getNumber_of_days().isEmpty() && !dataBean.getJob_offer().isEmpty()) {
                float paidAmount = Float.parseFloat(dataBean.getNumber_of_days()) * Float.parseFloat(dataBean.getJob_offer()) * Float.parseFloat(dataBean.getJob_time_duration());
                holder.tvBudget.setText("$ " + paidAmount);

            } else if (!dataBean.getJob_offer().isEmpty()) {
                holder.tvBudget.setText("$ " + dataBean.getJob_offer());

            } else {
                holder.tvBudget.setText("$ " + dataBean.getJob_budget());
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
        private TextView btnSendRequest;
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
                    listener.ConfirmJobItemOnClick(dataBeanList.get(getAdapterPosition()).getJobId(), dataBeanList.get(getAdapterPosition()).getJob_type());
                }
            });

           /* btnSendRequest.setOnClickListener(new View.OnClickListener() {
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
            });*/

        }

        public ViewDataBinding getBinding() {

            return binding;

        }
    }

    public interface ConfirmJobListener {
        void ConfirmJobItemOnClick(String JobId, String pos);
    }

}


