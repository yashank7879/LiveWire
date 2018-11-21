package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livewire.R;

/**
 * Created by mindiii on 11/16/18.
 */

public class CompletedJobAdapter extends RecyclerView.Adapter<CompletedJobAdapter.MyViewHolder> {
    CompleteJobClientListener listener;
    public CompletedJobAdapter(Context mCotext,CompleteJobClientListener listener) {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.completed_job_cell,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

    }


    @Override
    public int getItemCount() {
        return 20;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

   public interface CompleteJobClientListener{
        void completeJobMoreInfoListener();

    }
}
