package com.livewire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.model.CategoryModel;
import com.livewire.ui.fragments.HelpOfferedFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 10/2/18.
 */

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {
    private Context mContext;
    private List<CategoryModel> subCategoryList;
    public SubCategoryAdapter(Context mCon, List<CategoryModel> subCategoryList) {
        this.mContext = mCon;
        this.subCategoryList = subCategoryList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_cat_cell,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (subCategoryList.size() != 0){
            CategoryModel categoryModel = subCategoryList.get(position);
            holder.tvSubCat.setText(categoryModel.getCategoryName());

        }

    }


    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubCat;
        ImageView imgRemove;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvSubCat = itemView.findViewById(R.id.tv_sub_cat);
            imgRemove = itemView.findViewById(R.id.btn_remove);
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subCategoryList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }
}
