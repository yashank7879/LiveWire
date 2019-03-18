package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.RequestResponceClient;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 10/16/18.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    private Context mContext;
    private List<RequestResponceClient.DataBean> requestList;
    private RequestAcceptIgnorListner listner;

    public RequestAdapter(Context mContext, List<RequestResponceClient.DataBean> requestList,RequestAcceptIgnorListner listner) {
        this.mContext = mContext;
        this.requestList = requestList;
        this.listner = listner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_cell,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if (requestList.size() != 0){
            if (requestList.get(position).getJob_confirmed().equals("0") || requestList.get(position).getJob_confirmed().equals("3")) {
                holder.rlRequest.setVisibility(View.VISIBLE);
                RequestResponceClient.DataBean dataBean = requestList.get(position);
                holder.tvName.setText(dataBean.getName());
                holder.tvDistance.setText(dataBean.getDistance_in_km() + " Km away");
                Picasso.with(holder.ivProfileImg.getContext()).load(dataBean.getProfileImage()).placeholder(R.drawable.ic_user).fit().into(holder.ivProfileImg);
            if (!dataBean.getRating().isEmpty()){
                holder.ratingBar.setRating(Float.parseFloat(dataBean.getRating()));
            }
            }
            holder.rlUserData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.OnClickMoreInfo(requestList.get(holder.getAdapterPosition()).getUserId());
                }
            });


        }
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivProfileImg;
        private TextView tvName;
        private RatingBar ratingBar;
        private TextView tvDistance;
        private Button btnIgnore;
        private Button btnAccept;
        private RelativeLayout rlRequest;
        private RelativeLayout rlUserData;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivProfileImg =itemView.findViewById(R.id.iv_profile_img);
            tvName =  itemView.findViewById(R.id.tv_name);
            ratingBar =  itemView.findViewById(R.id.rating_bar);
            tvDistance =  itemView.findViewById(R.id.tv_distance);
            btnIgnore =itemView.findViewById(R.id.btn_ignore);
            btnAccept =  itemView.findViewById(R.id.btn_accept);
            rlRequest =  itemView.findViewById(R.id.rl_request);
            rlUserData =  itemView.findViewById(R.id.rl_user_data);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { //"" request accept = 1 "
                  listner.OnClickRequestAccept("1",requestList.get(getAdapterPosition()).getUserId(), getAdapterPosition());
                 // requestList.remove(getAdapterPosition());
                }
            });
            btnIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//"" request reject = 2 "
                  listner.OnClickRequestAccept("2", requestList.get(getAdapterPosition()).getUserId(),getAdapterPosition());
                    //requestList.remove(getAdapterPosition());
                }
            });
        }
    }
   public interface RequestAcceptIgnorListner{
        void OnClickRequestAccept(String s, String userId, int position);
        void OnClickMoreInfo(String userId);

    }
}
