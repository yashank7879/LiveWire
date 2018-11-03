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
import com.livewire.model.CategoryBean;
import com.livewire.responce.MyProfileResponce;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.Constant.setListViewHeightBasedOnChildren;


/**
 * Created by mindiii on 11/1/18.
 */

public class ShowSkillsAdapter extends RecyclerView.Adapter<ShowSkillsAdapter.MyViewHolder> {
    private Context mContext;
    private List<MyProfileResponce.DataBean.CategoryBean> categoryList;

    public ShowSkillsAdapter(Context mContext, ArrayList<MyProfileResponce.DataBean.CategoryBean> categoryList) {
        this.mContext = mContext;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_sub_cat,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyProfileResponce.DataBean.CategoryBean categoryBean = categoryList.get(position);
        if (categoryBean != null){
            holder.tv_category.setText(categoryBean.getCategoryName());
            holder.listView.setAdapter(new ShowSubCetAdapter(mContext, categoryBean.getSubcat()));
            setListViewHeightBasedOnChildren(holder.listView);//increase height at run time
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_category;
        private ListView listView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category = itemView.findViewById(R.id.tv_category);
            listView = itemView.findViewById(R.id.listView);
        }
    }
}
