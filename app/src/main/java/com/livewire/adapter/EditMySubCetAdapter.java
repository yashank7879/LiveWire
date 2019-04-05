package com.livewire.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.model.AddedSkillBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 11/3/18.
 */

class EditMySubCetAdapter extends BaseAdapter {
    private List<AddedSkillBean.SubCatagory> objects = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    private SubCategoryListner listner;

    public EditMySubCetAdapter(Context context, List<AddedSkillBean.SubCatagory> subService, SubCategoryListner lister) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.objects = subService;
        this.listner = lister;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public AddedSkillBean.SubCatagory getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sub_category_cell, null);
            convertView.setTag(new EditMySubCetAdapter.ViewHolder(convertView));
        }
        initializeViews(getItem(position), position, (EditMySubCetAdapter.ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(final AddedSkillBean.SubCatagory object, final int position, final ViewHolder holder) {
        if (object == null) {
            holder.subCategoryText.setVisibility(View.INVISIBLE);
        } else {
            holder.subCategoryText.setTag(position);
            holder.subCategoryText.setVisibility(View.VISIBLE);
            holder.subCategoryText.setText(object.getSubName());
            String price= "$"+object.getMin_rate()+" - $"+object.getMax_rate();
            holder.tvPrice.setText(price);
            holder.ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   objects.remove(position);
                   notifyDataSetChanged();
                }
            });
            // holder.subItemText.setText(object.getName());
        }
    }

    protected class ViewHolder {

        private TextView subCategoryText;
        private TextView tvPrice;
        private LinearLayout containerLay;
        private ImageView ivCancel;


        public ViewHolder(View view) {

            subCategoryText = view.findViewById(R.id.tv_sub_category);
            ivCancel = view.findViewById(R.id.iv_cancel);
            tvPrice = view.findViewById(R.id.tv_price);

            ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isListEmpty= false;
                    if (objects.size() == 0){
                        isListEmpty = true;
                    }
                    listner.subCategoryOnClickListener((Integer) subCategoryText.getTag() , isListEmpty);
                }
            });
        }
    }

    public interface SubCategoryListner {
        void subCategoryOnClickListener(int pos, boolean vlue);
    }
}
