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

import com.livewire.R;
import com.livewire.model.Chat;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.squareup.picasso.Picasso;

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
        //holder.tvMsg.setText(chat.lastMsg);

        Picasso.with(holder.ivProfileImage.getContext()).load(chat.profilePic).fit().into(holder.ivProfileImage);
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

        public MyViewHolder(View view) {
            super(view);
            chatCellLayout =  view.findViewById(R.id.chat_cell_layout);
            ivProfileImage =  view.findViewById(R.id.iv_favorite_image);
            tvName =  view.findViewById(R.id.tv_name);
            tvMsg =  view.findViewById(R.id.tv_msg);
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

