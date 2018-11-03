package com.livewire.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 11/1/18.
 */

public class CategoryBean implements Serializable {

    /**
     * parentCategoryId : 1
     * categoryName : Family
     * parent_id : 0
     * subcat : [{"categoryId":"7","categoryName":"Babysitting","parent_id":"1","max_rate":"100","min_rate":"50"},{"categoryId":"9","categoryName":"Transport","parent_id":"1","max_rate":"500","min_rate":"450"}]
     */

    private String parentCategoryId;
    private String categoryName;
    private String parent_id;
    private List<SubcatBean> subcat;

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public List<SubcatBean> getSubcat() {
        return subcat;
    }

    public void setSubcat(List<SubcatBean> subcat) {
        this.subcat = subcat;
    }

    public static class SubcatBean implements Serializable {
        /**
         * categoryId : 7
         * categoryName : Babysitting
         * parent_id : 1
         * max_rate : 100
         * min_rate : 50
         */

        private String categoryId;
        private String categoryName;
        private String parent_id;
        private String max_rate;
        private String min_rate;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getParent_id() {
            return parent_id;
        }

        public void setParent_id(String parent_id) {
            this.parent_id = parent_id;
        }

        public String getMax_rate() {
            return max_rate;
        }

        public void setMax_rate(String max_rate) {
            this.max_rate = max_rate;
        }

        public String getMin_rate() {
            return min_rate;
        }

        public void setMin_rate(String min_rate) {
            this.min_rate = min_rate;
        }
    }
}
