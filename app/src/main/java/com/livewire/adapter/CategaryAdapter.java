package com.livewire.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.model.AddedSkillBean;

import java.util.ArrayList;

import static com.livewire.utils.Constant.setListViewHeightBasedOnChildren;

/**
 * Created by mindiii on 9/20/18.
 */

public class CategaryAdapter extends RecyclerView.Adapter<CategaryAdapter.MyViewHolder> {

    private ArrayList<AddedSkillBean> categaryList;
    private Context mContext;
    private boolean expand = false;

    public CategaryAdapter(Context context, ArrayList<AddedSkillBean> categaryList) {
        this.categaryList = categaryList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_cell, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AddedSkillBean addedSkillBean = categaryList.get(position);
        addedSkillBean.getSubCatagories();
        holder.tv_category.setText(addedSkillBean.getName());
        holder.listView.setAdapter(new MySubCetAdapter(mContext, addedSkillBean.getSubCatagories(), new MySubCetAdapter.SubCategoryListner() {
            @Override
            public void subCategoryOnClickListener(int pos) {
                AddedSkillBean data = categaryList.get(holder.getAdapterPosition());
                if (data.getSubCatagories().size() == 1) {
                    categaryList.remove(holder.getAdapterPosition());
                    notifyItemChanged(holder.getAdapterPosition());
                    notifyItemRemoved(pos);
                } else {
                    data.getSubCatagories().remove(pos);
                    notifyItemChanged(holder.getAdapterPosition());
                    notifyItemRemoved(pos);
                    /*holder.listView.getAdapter().notify();*/
                }
                /*holder.getAdapterPosition();*/
            }
        }));

        setListViewHeightBasedOnChildren(holder.listView);
     /*   if (addedSkillBean.isVisible()) {
            holder.listView.setVisibility(View.VISIBLE);
        } else {
            holder.listView.setVisibility(View.GONE);
        }*/
    }

    @Override
    public int getItemCount() {
        return categaryList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_category;
        public ListView listView;

        public MyViewHolder(View view) {
            super(view);
            tv_category = (TextView) view.findViewById(R.id.tv_category);
            listView = (ListView) view.findViewById(R.id.listView);
            tv_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*AddedSkillBean addedSkillBean = categaryList.get(getAdapterPosition());
                    if (addedSkillBean.isVisible()) {
                        addedSkillBean.setVisible(false);
                    } else {
                        addedSkillBean.setVisible(true);
                    }
                    notifyDataSetChanged();*/
                }
            });

        }
    }



}