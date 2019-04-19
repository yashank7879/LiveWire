package com.livewire.adapter;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.livewire.R;
import com.livewire.model.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by mindiii on 12/21/18.
 */

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Chat> chatList;
    private String myUid;
    private int VIEW_TYPE_ME = 1;
    private int VIEW_TYPE_OTHER = 2;
    boolean ishideName ;
    GetDateStatus getDateStatus;

    public ChattingAdapter(Context mContext, ArrayList<Chat> chatList, String myUid, GetDateStatus getDateStatus, boolean ishideName) {
        this.chatList = chatList;
        this.mContext = mContext;
        this.myUid = myUid;
        this.getDateStatus = getDateStatus;
        this.ishideName = ishideName;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(chatList.get(position).uid, myUid)) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;

        if (viewType == VIEW_TYPE_ME) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_right_side_view, viewGroup, false);
            return new MyViewHolder(view);
        } else if (viewType == VIEW_TYPE_OTHER) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_left_side_view, viewGroup, false);
            return new OtherViewHolder(view);
        }

        return new OtherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        int pos = position - 1;
        int tempPos = (pos == -1) ? pos + 1 : pos;

        if (TextUtils.equals(chat.uid, myUid)) {
            ((MyViewHolder) holder).myBindData(chat, tempPos);
        } else {
            ((OtherViewHolder) holder).otherBindData(chat, tempPos);
        }
    }




    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView my_message, my_date_time_;
        RelativeLayout ly_my_image_view;
        ImageView iv_my_side_img, iv_msg_tick;
        TextView tv_days_status;
        ProgressBar my_progress;

        public MyViewHolder(View itemView) {
            super(itemView);
            my_message = itemView.findViewById(R.id.my_message);
            ly_my_image_view = itemView.findViewById(R.id.ly_my_image_view);
            iv_my_side_img = itemView.findViewById(R.id.iv_my_side_img);
            my_date_time_ = itemView.findViewById(R.id.my_date_time_);
            tv_days_status = itemView.findViewById(R.id.tv_days_status);
           // iv_msg_tick = itemView.findViewById(R.id.iv_msg_tick);
            my_progress = itemView.findViewById(R.id.my_progress);

        }

        void myBindData(final Chat chat, int tempPos) {
            if (!chat.imageUrl.isEmpty()) {

                ly_my_image_view.setVisibility(View.VISIBLE);
                my_message.setVisibility(View.GONE);

                my_progress.setVisibility(View.VISIBLE);

                Glide.with(mContext).load(chat.imageUrl).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        my_progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        my_progress.setVisibility(View.GONE);
                        return false;
                    }
                }).apply(new RequestOptions().placeholder(R.drawable.ic_user)).into(iv_my_side_img);


            } else {
                ly_my_image_view.setVisibility(View.GONE);
                my_message.setVisibility(View.VISIBLE);
                my_message.setText(chat.message);
            }


           /* if (chat.isMsgReadTick == 1) {
                iv_msg_tick.setImageResource(R.drawable.ico_msg_received);
            } else if (chat.isMsgReadTick == 2) {
                iv_msg_tick.setImageResource(R.drawable.ico_msg_read);
            } else {
                iv_msg_tick.setImageResource(R.drawable.ico_msg_sent);
            }
*/

            SimpleDateFormat sd = new SimpleDateFormat("hh:mm a");
            try {
                String date = sd.format(new Date((Long) chat.timestamp));
                my_date_time_.setText(date);

            } catch (Exception e) {

            }

            iv_my_side_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    full_screen_photo_dialog(chat.imageUrl);
                }
            });
            getDateStatus.currentDateStatus(chat.timestamp);

            /* SimpleDateFormat sd1 = new  SimpleDateFormat("dd MMMM yyyy");
             try {
                 String date1 = sd1.format(new Date((Long) chat.timestamp));
                 isTodaysDate(date1,tv_days_status);
             }catch (Exception e){

             }
*/

            if (!chat.banner_date.equals(chatList.get(tempPos).banner_date)) {
                tv_days_status.setText(chat.banner_date);
                tv_days_status.setVisibility(View.VISIBLE);
            } else {
                tv_days_status.setVisibility(View.GONE);
            }

            if(!ishideName){
                iv_msg_tick.setVisibility(View.GONE);
            }

        }

        public void full_screen_photo_dialog(String image_url) {
            final Dialog openDialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            openDialog.getWindow().getAttributes().windowAnimations = R.style.pauseDialogAnimation;
            openDialog.setContentView(R.layout.full_image_view_dialog);
            ImageView iv_back = openDialog.findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog.dismiss();
                }
            });

            PhotoView photoView = openDialog.findViewById(R.id.photo_view);
            if (!image_url.equals("")) {
                Glide.with(mContext).load(image_url).apply(new RequestOptions().placeholder(R.drawable.ic_user)).into(photoView);
            }
            openDialog.show();

        }
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {
        TextView other_message, other_date_time_;
        LinearLayout ly_other_image_view,ly_msg_view;
        ImageView iv_other_side_img;
        TextView tv_days_status,other_name,other_name_;
        ProgressBar other_progress;

        public OtherViewHolder(View itemView) {
            super(itemView);
            other_message = itemView.findViewById(R.id.other_message);
            ly_other_image_view = itemView.findViewById(R.id.ly_other_image_view);
            ly_msg_view = itemView.findViewById(R.id.ly_msg_view);
            iv_other_side_img = itemView.findViewById(R.id.iv_other_side_img);
            other_date_time_ = itemView.findViewById(R.id.other_date_time_);
            tv_days_status = itemView.findViewById(R.id.tv_days_status);
            other_progress = itemView.findViewById(R.id.other_progress);
            other_name = itemView.findViewById(R.id.other_name);
            other_name_ = itemView.findViewById(R.id.other_name_);
        }

        public void otherBindData(final Chat chat, int tempPos) {

            if (!chat.imageUrl.isEmpty()) {

                ly_other_image_view.setVisibility(View.VISIBLE);
                other_message.setVisibility(View.GONE);
                ly_msg_view.setVisibility(View.GONE);

                other_progress.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(chat.imageUrl).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        other_progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        other_progress.setVisibility(View.GONE);
                        return false;
                    }
                }).apply(new RequestOptions().placeholder(R.drawable.ic_user)).into(iv_other_side_img);
            } else {
                ly_other_image_view.setVisibility(View.GONE);
                other_message.setVisibility(View.VISIBLE);
                ly_msg_view.setVisibility(View.VISIBLE);
                other_message.setText(chat.message);
            }

            SimpleDateFormat sd = new SimpleDateFormat("hh:mm a");
            try {
                String date = sd.format(new Date((Long) chat.timestamp));
                other_date_time_.setText(date);

            } catch (Exception ignored) {

            }

            iv_other_side_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    full_screen_photo_dialog(chat.imageUrl);
                }
            });

           getDateStatus.currentDateStatus(chat.timestamp);

            if (!chat.banner_date.equals(chatList.get(tempPos).banner_date)) {
                tv_days_status.setText(chat.banner_date);
                tv_days_status.setVisibility(View.VISIBLE);
            } else {
                tv_days_status.setVisibility(View.GONE);
            }

            if(ishideName){
                other_name.setVisibility(View.GONE);
                other_name_.setVisibility(View.GONE);
            }else {
                other_name.setVisibility(View.VISIBLE);
                other_name_.setVisibility(View.VISIBLE);
                other_name.setText(chat.name+"");
                other_name_.setText(chat.name+"");
            }
        }

        public void full_screen_photo_dialog(String image_url) {

            final Dialog openDialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            //final Dialog openDialog = new Dialog(mContext);
            openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            openDialog.getWindow().getAttributes().windowAnimations = R.style.pauseDialogAnimation;
            openDialog.setContentView(R.layout.full_image_view_dialog);
            ImageView iv_back = openDialog.findViewById(R.id.iv_back);
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog.dismiss();
                }
            });

            PhotoView photoView = openDialog.findViewById(R.id.photo_view);
            if (!image_url.equals("")) {
                Glide.with(mContext).load(image_url).apply(new RequestOptions().placeholder(R.drawable.ic_user)).into(photoView);

            }
            openDialog.show();

        }
    }
    public interface GetDateStatus{
        void currentDateStatus(Object timestamp);
    }
}
