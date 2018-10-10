package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.model.WeekListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 10/9/18.
 */

public class SelectWeekAdapter extends RecyclerView.Adapter<SelectWeekAdapter.MyViewHolder> {
    private List<WeekListModel> weekList;
    private Context mContext;

    public SelectWeekAdapter(Context mContext, ArrayList<WeekListModel> weekList) {
        this.mContext = mContext;
        this.weekList = weekList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_cell, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String week = weekList.get(position).getWeekDays();
        holder.checkBox.setChecked(weekList.get(position).isWeekDay());
        holder.weekDays.setText(week);

    }


    @Override
    public int getItemCount() {
        return weekList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView weekDays;
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            weekDays = itemView.findViewById(R.id.tv_week_days);
            checkBox = itemView.findViewById(R.id.cb_week_days);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        weekList.get(getAdapterPosition()).setisWeekDay(true);
                    }else {
                        weekList.get(getAdapterPosition()).setisWeekDay(false);
                    }
                }
            });
        }
    }
}
