package com.livewire.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mindiii on 9/25/18.
 */

public class SubCategoryModel {
    private String category_id;
    private float min_rate;
    private float max_rate;

    public String getCategory_id() {
        return category_id;
    }

    public float getMin_rate() {
        return min_rate;
    }

    public float getMax_rate() {
        return max_rate;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setMin_rate(float min_rate) {
        this.min_rate = min_rate;
    }

    public void setMax_rate(float max_rate) {
        this.max_rate = max_rate;
    }


}
