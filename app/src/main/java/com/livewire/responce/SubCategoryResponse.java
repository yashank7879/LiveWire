package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 10/2/18.
 */

public class SubCategoryResponse {
    /**
     * status : success
     * message : OK
     * data : [{"categoryId":"7","categoryName":"Babysitting"},
     * {"categoryId":"8","categoryName":"Tuition / Extra lessons"},
     * {"categoryId":"9","categoryName":"Transport"},
     * {"categoryId":"10","categoryName":"Counselling"},
     * {"categoryId":"11","categoryName":"DIY / home maintenance"},
     * {"categoryId":"12","categoryName":"Gardening"},
     * {"categoryId":"13","categoryName":"Cooking / Baking"},
     * {"categoryId":"14","categoryName":"Pet Care"},
     * {"categoryId":"15","categoryName":"Sewing / Knitting"},
     * {"categoryId":"16","categoryName":"Decorating"},
     * {"categoryId":"17","categoryName":"Travel Companion"},
     * {"categoryId":"18","categoryName":"Transport"},
     * {"categoryId":"19","categoryName":"Telephonic"},
     * {"categoryId":"20","categoryName":"Clerical"},
     * {"categoryId":"21","categoryName":"Children"},
     * {"categoryId":"22","categoryName":"Wildlife"},
     * {"categoryId":"23","categoryName":"Elderly"}]
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
         * categoryId : 7
         * categoryName : Babysitting
         */

        private String categoryId;
        private String categoryName;
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

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

        @Override
        public String toString() {
            return getCategoryName();
        }
    }
}
