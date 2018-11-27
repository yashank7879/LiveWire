package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.responce.StripeSaveCardResponce;

import java.util.List;

/**
 * Created by mindiii on 11/22/18.
 */

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.MyViewHolder> {
    private Context mContext;
    // private List<Boolean> creditcardList;
    private List<StripeSaveCardResponce.DataBean> creditcardList;
    private CardDetailInterface listner;

    public CreditCardAdapter(Context mContext, List<StripeSaveCardResponce.DataBean> creditcardList, CardDetailInterface listner) {
        this.mContext = mContext;
        this.creditcardList = creditcardList;
        this.listner = listner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_pay_card, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int i) {

        if (creditcardList.size() != 0) {
            StripeSaveCardResponce.DataBean dataBean = creditcardList.get(i);
            holder.tvCardNumber.setText(dataBean.getLast4());
            holder.tvDate.setText(dataBean.getExp_month() + "/" +dataBean.getExp_year());

            if(!dataBean.isMoreDetail()){
                holder.cardExtraDetail.setVisibility(View.VISIBLE);
                holder.ivCheckInfo.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_card_check));

            }else{
                holder.cardExtraDetail.setVisibility(View.GONE);
                holder.ivCheckInfo.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.circle_holo_gray));
            }
        }

    }


    @Override
    public int getItemCount() {
        return creditcardList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mainClick;
        private ImageView ivCheckInfo;
        private ImageView ivDeleteCard;
        private TextView tvCardType;
        private TextView tvCardNumber;
        private LinearLayout cardExtraDetail;
        private TextView tvDate;
        private TextView edCvv;
        private boolean isInfo = true;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mainClick = (RelativeLayout) itemView.findViewById(R.id.main_click);
            ivCheckInfo = itemView.findViewById(R.id.iv_check_info);
            ivDeleteCard = itemView.findViewById(R.id.iv_delete_card);
            tvCardType = (TextView) itemView.findViewById(R.id.tv_card_type);
            tvCardNumber = (TextView) itemView.findViewById(R.id.tv_card_number);
            cardExtraDetail = (LinearLayout) itemView.findViewById(R.id.ll_card_extra_detail);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            edCvv = itemView.findViewById(R.id.ed_cvv);

            ivCheckInfo.setOnClickListener(new View.OnClickListener() {//""""" show extra detail """"""""//
                @Override
                public void onClick(View view) {
                    if (creditcardList.get(getAdapterPosition()).isMoreDetail()){

                        listner.moreDetailOnClick(getAdapterPosition(),false);

                    }
                }
            });

            ivDeleteCard.setOnClickListener(new View.OnClickListener() {//"""""""" delete save card """""""""""//
                @Override
                public void onClick(View view) {
                    listner.deleteSaveCard(getAdapterPosition(), creditcardList.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    public interface CardDetailInterface {
        void moreDetailOnClick(int pos, boolean value);
        void deleteSaveCard(int pos,String customerId);
    }
}


/*
protected class ViewHolder {
    private RelativeLayout mainClick;
    private RelativeLayout ivCheckInfo;
    private TextView tvCardType;
    private TextView tvCardNumber;
    private LinearLayout cardExtraDetail;
    private TextView tvDate;
    private EditText edCvv;

    public ViewHolder(View view) {
        mainClick = (RelativeLayout) view.findViewById(R.id.main_click);
        ivCheckInfo = (RelativeLayout) view.findViewById(R.id.iv_check_info);
        tvCardType = (TextView) view.findViewById(R.id.tv_card_type);
        tvCardNumber = (TextView) view.findViewById(R.id.tv_card_number);
        cardExtraDetail = (LinearLayout) view.findViewById(R.id.card_extra_detail);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        edCvv = (EditText) view.findViewById(R.id.ed_cvv);
    }
}*/
