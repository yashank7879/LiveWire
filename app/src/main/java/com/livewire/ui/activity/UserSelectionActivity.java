package com.livewire.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.utils.Constant;
import com.loopeer.shadow.ShadowView;

public class UserSelectionActivity extends AppCompatActivity implements View.OnClickListener {
   private CardView wantToHireCardView;
    private CardView wantToWorkCardView;
     private ImageView ivWantToHire;
    private ImageView ivWantToWork;
    private TextView tvWantToWork;
    private  TextView tvWantToHire;
    private  TextView tvHelpNeede;
    private  TextView tvHappyToHelp;
    private Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        intializeViews();

    }

    private void intializeViews() {
        TextView tvLiveWire = findViewById(R.id.tv_live_wire);
        wantToWorkCardView = findViewById(R.id.happy_to_help_cardView);
        wantToHireCardView = findViewById(R.id.help_need_cardView);
        ivWantToWork = findViewById(R.id.iv_happy_to_help);
        ivWantToHire = findViewById(R.id.iv_help_needed);
        tvWantToWork = findViewById(R.id.tv_want_to_work);
        tvWantToHire = findViewById(R.id.tv_want_to_hire);

        tvHelpNeede = findViewById(R.id.tv_help_needed);
        tvHappyToHelp = findViewById(R.id.tv_happy_to_help);

        TextView liveWireTerms = findViewById(R.id.tv_lw_tems);

        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);



       // liveWireTerms.setPaintFlags(liveWireTerms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);// to add underline

        wantToWorkCardView.setOnClickListener(this);
        wantToHireCardView.setOnClickListener(this);
        tvLiveWire.setText(Constant.liveWireText(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.help_need_cardView:
                Intent intent = new Intent(this,LoginActivity.class);
                intent.putExtra("UserKey","client");
                startActivity(intent);

                wantToHireCardView.setCardElevation(8);
                wantToWorkCardView.setCardElevation(2);


          /*      wantToHireCardView.setCardElevation(3);
                wantToWorkCardView.setCardElevation(1);*/

              /*  wantToHireCardView.setShadowMargin(8,10,8,8);
                wantToHireCardView.setShadowRadius(12);
                wantToWorkCardView.setShadowMargin(2,2,2,2);
                wantToWorkCardView.setShadowRadius(5);*/

                tvWantToHire.setTextColor(getResources().getColor(R.color.colorGreen));
                tvHelpNeede.setTextColor(getResources().getColor(R.color.colorGreen));

                tvHappyToHelp.setTextColor(getResources().getColor(R.color.colorLightGray));
                tvWantToWork.setTextColor(getResources().getColor(R.color.colorLightGray));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivWantToHire.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    ivWantToWork.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
                }
                break;
            case R.id.happy_to_help_cardView:

                Intent intent1 = new Intent(this,LoginActivity.class);
                intent1.putExtra("UserKey","worker");
                startActivity(intent1);
                wantToHireCardView.setCardElevation(2);
                wantToWorkCardView.setCardElevation(8);
              /*  wantToHireCardView.setShadowMargin(2,2,2,2);
                wantToHireCardView.setShadowRadius(5);
                wantToWorkCardView.setShadowMargin(8,10,8,8);
                wantToWorkCardView.setShadowRadius(12);*/

                tvHelpNeede.setTextColor(getResources().getColor(R.color.colorLightGray));
                tvHappyToHelp.setTextColor(getResources().getColor(R.color.colorGreen));

                tvWantToHire.setTextColor(getResources().getColor(R.color.colorLightGray));
                tvWantToWork.setTextColor(getResources().getColor(R.color.colorGreen));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivWantToHire.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
                    ivWantToWork.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                }

                break;
        }
    }
}
