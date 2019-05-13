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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.NearYouResponce;
import com.livewire.ui.activity.NearYouClientActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 10/10/18.
 */

public class NearYouAdapter extends RecyclerView.Adapter<NearYouAdapter.MyViewHolder> {
    private List<NearYouResponce.DataBean> nearYouList;
    private Context mContext;
    private NearYouRequestListener listener;

    public NearYouAdapter(Context mCon, List<NearYouResponce.DataBean> nearYouList, NearYouRequestListener listener) {
        this.mContext = mCon;
        this.nearYouList = nearYouList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.near_you_cell, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (nearYouList != null) {
            NearYouResponce.DataBean data = nearYouList.get(position);
            holder.tvCategory.setText(data.getCategory_name());
            holder.tvSubcategory.setText(data.getParent_category());
            holder.tvName.setText(data.getName());
            Picasso.with(holder.ivProfileImg.getContext()).load(data.getProfileImage()).into(holder.ivProfileImg);
            holder.tvDistance.setText(data.getDistance_in_km() + " Km");
            float rating =  data.getRating().isEmpty() ? 0:Float.parseFloat(data.getRating());
            holder.ratingBar.setRating(rating);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString minprice = new SpannableString("R" + data.getMin_rate());
            /*minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, minprice.length(), 0);
            //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
            builder.append(minprice);
            SpannableString toString = new SpannableString(" to ");
            builder.append(toString);

            SpannableString maxprice = new SpannableString("R" + data.getMax_rate());
            maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
            //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
            builder.append(maxprice);*/

            holder.tvRange.setText(minprice);


        }


    }


    @Override
    public int getItemCount() {
        return nearYouList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory;
        private TextView tvSubcategory;
        private TextView range;
        private TextView tvRange;
        private View view1Id;
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private LinearLayout llkmAway;
        private TextView tvDistance;
        private Button btnSendRequest;
        private RelativeLayout rlProfileDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCategory = (TextView) itemView.findViewById(R.id.tv_category);
            tvSubcategory = (TextView) itemView.findViewById(R.id.tv_subcategory);
            range = (TextView) itemView.findViewById(R.id.range);
            tvRange = (TextView) itemView.findViewById(R.id.tv_range);
            view1Id = (View) itemView.findViewById(R.id.view1_id);
            ivProfileImg = (CircleImageView) itemView.findViewById(R.id.iv_profile_img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            llkmAway = (LinearLayout) itemView.findViewById(R.id.llkm_away);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            btnSendRequest = (Button) itemView.findViewById(R.id.btn_send_request);
            rlProfileDetail = itemView.findViewById(R.id.rl_profile_detail);
            rlProfileDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                listener.userInfoDetail(nearYouList.get(getAdapterPosition()).getUserId());
                }
            });

            btnSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.requestBtnOnclick(nearYouList.get(getAdapterPosition()));
                }
            });

        }
    }

    public interface NearYouRequestListener {
        void requestBtnOnclick(NearYouResponce.DataBean response);

        void userInfoDetail(String userId);
    }

}


