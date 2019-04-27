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
import com.livewire.model.CategoryBean;
import com.livewire.responce.MyProfileResponce;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 11/1/18.
 */

class ShowSubCetAdapter extends BaseAdapter {
    private List<MyProfileResponce.DataBean.CategoryBean.SubcatBean> objects = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    public ShowSubCetAdapter(Context mContext, List<MyProfileResponce.DataBean.CategoryBean.SubcatBean> subcat) {
        this.context = mContext;
        this.objects = subcat;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public MyProfileResponce.DataBean.CategoryBean.SubcatBean getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sub_show_category_cell, null);
            convertView.setTag(new ShowSubCetAdapter.ViewHolder(convertView));
        }
        initializeViews(getItem(position), position, (ShowSubCetAdapter.ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(MyProfileResponce.DataBean.CategoryBean.SubcatBean object, int position, ViewHolder holder) {
        if (object == null) {
            holder.subCategoryText.setVisibility(View.INVISIBLE);
        } else {
            holder.subCategoryText.setTag(position);
            holder.subCategoryText.setVisibility(View.VISIBLE);
            holder.subCategoryText.setText(object.getCategoryName());
            String price= "R"+object.getMin_rate()+" - R"+object.getMax_rate();
            holder.tvPrice.setText(price);

            /*holder.ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   objects.remove(position);
                   notifyDataSetChanged();
                }
            });*/
            // holder.subItemText.setText(object.getName());
        }
    }

    public class ViewHolder {
        private TextView subCategoryText;
        private TextView tvPrice;
        private LinearLayout containerLay;
        public ViewHolder(View view) {
            subCategoryText = view.findViewById(R.id.tv_sub_category);
            tvPrice = view.findViewById(R.id.tv_price);
        }
    }
}
