package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.model.AddedSkillBean;

import java.util.ArrayList;

import static com.livewire.utils.Constant.getDayDifference;
import static com.livewire.utils.Constant.setListViewHeightBasedOnChildren;

/**
 * Created by mindiii on 11/3/18.
 */

public class EditCategaryAdapter extends RecyclerView.Adapter<EditCategaryAdapter.MyViewHolder> {

    private ArrayList<AddedSkillBean> categaryList;
    private Context mContext;
    private boolean expand = false;

    public EditCategaryAdapter(Context context, ArrayList<AddedSkillBean> categaryList) {
        this.categaryList = categaryList;
        this.mContext = context;
    }

    @Override
    public EditCategaryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_cell, parent, false);

        return new EditCategaryAdapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        AddedSkillBean addedSkillBean = categaryList.get(position);
        holder.tv_category.setText(addedSkillBean.getName());
        holder.listView.setAdapter(new EditMySubCetAdapter(mContext, addedSkillBean.getSubCatagories(), new EditMySubCetAdapter.SubCategoryListner() {
            @Override
            public void subCategoryOnClickListener(int pos, boolean value) {
                AddedSkillBean data = categaryList.get(holder.getAdapterPosition());
                if (data.getSubCatagories().size() == 0) {
                    categaryList.remove(holder.getAdapterPosition());
                    //notifyItemRemoved(pos);
                    notifyItemChanged(holder.getAdapterPosition());
                    notifyDataSetChanged();
                } else {
                   // data.getSubCatagories().remove(pos);
                   // notifyItemRemoved(pos);
                    notifyItemChanged(holder.getAdapterPosition());
                    notifyDataSetChanged();
                    /*holder.listView.getAdapter().notify();*/
                }

               /* if (value){
                    holder.tv_category.setVisibility(View.GONE);
                    notifyItemChanged(holder.getAdapterPosition());
                }else holder.tv_category.setVisibility(View.VISIBLE);
*/
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