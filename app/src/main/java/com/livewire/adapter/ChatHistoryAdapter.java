package com.livewire.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ServerValue;
import com.livewire.R;
import com.livewire.model.Chat;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.livewire.utils.Constant;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindiii on 12/21/18.
 */

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.MyViewHolder> {
    private Context mContext;
    private List<Chat> historyList;

    public ChatHistoryAdapter(Context mContext, List<Chat> historyList) {
        this.mContext = mContext;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_history_cell, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Chat chat = historyList.get(i);
        if (chat.image == 1) {
            holder.tvMsg.setText("Image");
        } else {
            holder.tvMsg.setText(chat.message);
        }
        holder.tvName.setText(chat.name);

        if (chat.unreadCount != 0) {
            holder.tvUnreadCount.setText("" + chat.unreadCount);
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
        }else holder.tvUnreadCount.setVisibility(View.GONE);
        //holder.tvMsg.setText(chat.lastMsg);

        Picasso.with(holder.ivProfileImage.getContext()).load(chat.profilePic).fit().into(holder.ivProfileImage);


        /*SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Object val=ServerValue.TIMESTAMP;
            String currentTime = sd.format(new Date((Lo ng) val));
            String crd = sd.format(new Date((Long) chat.timestamp));

           // holder.tvTime.setText(Constant.getDayDifference(crd,currentTime));

            holder.tv_time.setText(Constant.getDayDifference(crd,currentTime));

        } catch (Exception ignored) {

        }*/
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout chatCellLayout;
        private CircleImageView ivProfileImage;
        private TextView tvName;
        private TextView tvMsg;
        private TextView tv_time;
        private TextView tvUnreadCount;

        public MyViewHolder(View view) {
            super(view);
            chatCellLayout =  view.findViewById(R.id.chat_cell_layout);
            ivProfileImage =  view.findViewById(R.id.iv_favorite_image);
            tvName =  view.findViewById(R.id.tv_name);
            tvMsg =  view.findViewById(R.id.tv_msg);
            tv_time =  view.findViewById(R.id.tv_time);
            tvUnreadCount =  view.findViewById(R.id.tv_unread_count);
            chatCellLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChattingActivity.class);
                    intent.putExtra("otherUID", historyList.get(getAdapterPosition()).uid);
                    intent.putExtra("titleName", historyList.get(getAdapterPosition()).name);
                    intent.putExtra("profilePic", historyList.get(getAdapterPosition()).profilePic);
                    mContext.startActivity(intent);
                }
            });

        }
    }
}

