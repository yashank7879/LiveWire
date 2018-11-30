package com.livewire.ui.dialog;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.livewire.R;
import com.livewire.databinding.DialogReviewBinding;
import com.livewire.utils.Constant;
import com.livewire.utils.Validation;

/**
 * Created by mindiii on 11/15/18.
 */

public class ReviewDialog extends DialogFragment implements View.OnClickListener ,RatingBar.OnRatingBarChangeListener{
    DialogReviewBinding binding;
    ReviewDialogListner listner;
    private Context mContext;


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
        binding.ratingBar.setOnRatingBarChangeListener(this);
       // Toast.makeText(mContext, ""+binding.ratingBar.getRating(), Toast.LENGTH_SHORT).show();

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
                reviewValidations();

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

    private void reviewValidations() {
       if (Validation.isEmpty(binding.etDemo)){
            Constant.snackBar(binding.reviewLayout,"Please Write comment.");
        }else if (binding.ratingBar.getRating() == 0.0){
           Constant.snackBar(binding.reviewLayout,"Please Give Rating.");
       }
        else {
           listner.onReviewOnClick(binding.etDemo.getText().toString().trim(),binding.ratingBar.getRating(),binding.reviewLayout);
       }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

        //Toast.makeText(mContext, ""+v, Toast.LENGTH_SHORT).show();
    }


    public interface ReviewDialogListner{
      void onReviewOnClick(String text, float rating, LinearLayout layout);
    }
}
