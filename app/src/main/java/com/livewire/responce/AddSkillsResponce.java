package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 9/18/18.
 */

public class AddSkillsResponce {

    /**
     * status : success
     * message : OK
     * data : [{"categoryId":"1","categoryName":"family","parent_id":"0","subcat":[{"categoryId":"7","categoryName":"Babysitting","parent_id":"1"},{"categoryId":"8","categoryName":"Tuition / Extra lessons","parent_id":"1"},{"categoryId":"9","categoryName":"Transport","parent_id":"1"},{"categoryId":"10","categoryName":"Counselling","parent_id":"1"}]},{"categoryId":"2","categoryName":"hous & home","parent_id":"0","subcat":[{"categoryId":"11","categoryName":"DIY / home maintenance","parent_id":"2"},{"categoryId":"12","categoryName":"Gardening","parent_id":"2"},{"categoryId":"13","categoryName":"Cooking / Baking","parent_id":"2"},{"categoryId":"14","categoryName":"Pet Care","parent_id":"2"},{"categoryId":"15","categoryName":"Sewing / Knitting","parent_id":"2"},{"categoryId":"16","categoryName":"Decorating","parent_id":"2"}]},{"categoryId":"3","categoryName":"holiday / leisure","parent_id":"0","subcat":[{"categoryId":"17","categoryName":"Travel Companion","parent_id":"3"},{"categoryId":"18","categoryName":"Transport","parent_id":"3"}]},{"categoryId":"4","categoryName":"ADMIN","parent_id":"0","subcat":[{"categoryId":"19","categoryName":"Telephonic","parent_id":"4"},{"categoryId":"20","categoryName":"Clerical","parent_id":"4"}]},{"categoryId":"5","categoryName":"VOLUNTEER","parent_id":"0","subcat":[{"categoryId":"21","categoryName":"Children","parent_id":"5"},{"categoryId":"22","categoryName":"Wildlife","parent_id":"5"},{"categoryId":"23","categoryName":"Elderly","parent_id":"5"}]},{"categoryId":"6","categoryName":"Religious","parent_id":"0","subcat":[]}]
     */

    private String status;
    private String message;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * categoryId : 1
         * categoryName : family
         * parent_id : 0
         * subcat : [{"categoryId":"7","categoryName":"Babysitting","parent_id":"1"},
         * {"categoryId":"8","categoryName":"Tuition / Extra lessons","parent_id":"1"},
         * {"categoryId":"9","categoryName":"Transport","parent_id":"1"}
         * ,{"categoryId":"10","categoryName":"Counselling","parent_id":"1"}]
         */

        private String categoryId;
        private String categoryName;
        private String parent_id;
        private List<SubcatBean> subcat;

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

        @Override
        public String toString() {
            return categoryName;
        }

        public List<SubcatBean> getSubcat() {
            return subcat;
        }

        public void setSubcat(List<SubcatBean> subcat) {
            this.subcat = subcat;
        }

        public static class SubcatBean {
            /**
             * categoryId : 7
             * categoryName : Babysitting
             * parent_id : 1
             */

            private String categoryId;
            private String categoryName;
            private String parent_id;

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

            @Override
            public String toString() {
                return categoryName;
            }
        }
    }
}
