package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.FaqResponce;
import com.livewire.ui.activity.FaqsActivity;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {
    private Context mContext;
    private List<FaqResponce> faqResponceList;
    private FaqInterface faqInterface;

    public FaqAdapter(FaqsActivity mContext, List<FaqResponce> faqResponceList, FaqInterface faqInterface) {
        this.mContext = mContext;
        this.faqResponceList = faqResponceList;
        this.faqInterface = faqInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.faq_cell, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        FaqResponce faqResponce = faqResponceList.get(i);
        if (faqResponce.isTitle()) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(faqResponce.getTittle());
            holder.tvQuestion.setText(faqResponce.getSubQue().get(0).getSubQuestion());
            holder.tvAns.setText(faqResponce.getSubQue().get(0).getAns());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
            holder.tvTitle.setText(faqResponce.getTittle());
            holder.tvQuestion.setText(faqResponce.getSubQue().get(0).getSubQuestion());
            holder.tvAns.setText(faqResponce.getSubQue().get(0).getAns());
        }

        if (faqResponce.isVisible()){
            holder.ivCollaps.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_minus));
            holder.tvQuestion.setTextColor(ContextCompat.getColor(mContext,R.color.colorGreen));
            holder.rlAns.setVisibility(View.VISIBLE);
        }else  {
            holder.ivCollaps.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_add));
            holder.rlAns.setVisibility(View.GONE);
            holder.tvQuestion.setTextColor(ContextCompat.getColor(mContext,R.color.colorDarkBlack));

        }


        holder.ivCollaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faqInterface.cellOnClick(faqResponce.isVisible(),faqResponce);
            }
        });


        holder.tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faqInterface.cellOnClick(faqResponce.isVisible(),faqResponce);
            }
        });
    }


    @Override
    public int getItemCount() {
        return faqResponceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private RelativeLayout rlQuestion;
        private TextView tvQuestion;
        private ImageView ivCollaps;
        private RelativeLayout rlAns;
        private TextView tvAns;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            rlQuestion = (RelativeLayout) view.findViewById(R.id.rl_question);
            tvQuestion = (TextView) view.findViewById(R.id.tv_question);
            ivCollaps = (ImageView) view.findViewById(R.id.iv_collaps);
            rlAns = (RelativeLayout) view.findViewById(R.id.rl_ans);
            tvAns = (TextView) view.findViewById(R.id.tv_ans);
        }
    }

    public interface FaqInterface {
        void cellOnClick(boolean value, FaqResponce model);

    }
}

