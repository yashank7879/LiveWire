package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 11/30/18.
 */

public class GetReviewResponce {
    /**
     * status : success
     * message : Ok
     * data : [{"userId":null,"name":null,"reviewId":"1","rating":"3","review_description":"I am not sure if you have","crd":"2018-11-24 10:11:08","profileImage":"https://livewire.work/./uploads/profile/default.png"}]
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
         * userId : null
         * name : null
         * reviewId : 1
         * rating : 3
         * review_description : I am not sure if you have
         * crd : 2018-11-24 10:11:08
         * profileImage : https://livewire.work/./uploads/profile/default.png
         */

        private String userId;
        private String name;
        private String reviewId;
        private String rating;
        private String review_description;
        private String crd;
        private String profileImage;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReviewId() {
            return reviewId;
        }

        public void setReviewId(String reviewId) {
            this.reviewId = reviewId;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getReview_description() {
            return review_description;
        }

        public void setReview_description(String review_description) {
            this.review_description = review_description;
        }

        public String getCrd() {
            return crd;
        }

        public void setCrd(String crd) {
            this.crd = crd;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }
    }
}
