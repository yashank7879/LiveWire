package com.livewire.ui.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livewire.R;
import com.livewire.databinding.DialogReviewBinding;
import com.livewire.utils.Constant;

/**
 * Created by mindiii on 11/15/18.
 */

public class ReviewDialog extends DialogFragment implements View.OnClickListener {
    DialogReviewBinding binding;
    ReviewDialogListner listner;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
       binding = DataBindingUtil.inflate(inflater,R.layout.dialog_review, container, false);
      //  View view = inflater.inflate(R.layout.dialog_review, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSubmit.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
                Constant.hideSoftKeyBoard(mContext,binding.etDemo);
                listner.onReviewOnClick(binding.etDemo.getText().toString().trim());
                break;
            case R.id.tv_cancel:
                Constant.hideSoftKeyBoard(mContext,binding.etDemo);
                getDialog().dismiss();
                break;
                default:
        }
    }

    public void getReviewInfo(ReviewDialogListner listner) {
        this.listner = listner;

    }

    public interface ReviewDialogListner{
      void onReviewOnClick(String text);
    }
}
