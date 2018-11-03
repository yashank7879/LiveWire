package com.livewire.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by mindiii on 9/20/18.
 */

public class AddedSkillBean {

    private String catageryId;
    private String name;
    private ArrayList<SubCatagory> subCatagories;
    private boolean isVisible = false;
    private float min_rate;
    private float max_rate;

    public String getCatageryId() {
        return catageryId;
    }

    public void setCatageryId(String catageryId) {
        this.catageryId = catageryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SubCatagory> getSubCatagories() {
        return subCatagories;
    }

    public void setSubCatagories(ArrayList<SubCatagory> subCatagories) {
        this.subCatagories = subCatagories;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public static class SubCatagory {
        @SerializedName("category_id")
        private String subCatId;
        private String subName;
        private float min_rate;
        private float max_rate;

        public String getSubCatId() {
            return subCatId;
        }

        public void setSubCatId(String subCatId) {
            this.subCatId = subCatId;
        }

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
        }

        public float getMin_rate() {
            return min_rate;
        }

        public void setMin_rate(float min_rate) {
            this.min_rate = min_rate;
        }

        public float getMax_rate() {
            return max_rate;
        }

        public void setMax_rate(float max_rate) {
            this.max_rate = max_rate;
        }

        @Override
        public String toString() {
            return subName;
        }
    }
}
