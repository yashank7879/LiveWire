package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 10/10/18.
 */

public class NearYouResponce {
    /**
     * status : success
     * message : OK
     * data : [{"userId":"1","name":"tushar","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/yAjRdCMZbofgOQu4.jpg","category_name":"Transport","parent_category":"family","max_rate":"500","min_rate":"450","distance_in_km":"125.9","job_confirmed":3},{"userId":"14","name":"Narendra","profileImage":"http://dev.livewire.work/./uploads/profile/default.png","category_name":"Transport","parent_category":"family","max_rate":"4","min_rate":"2","distance_in_km":"5201.7","job_confirmed":3},{"userId":"92","name":"Soham","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/3mwGHZ8uBlbrJIxs.jpeg","category_name":"Transport","parent_category":"family","max_rate":"22","min_rate":"21","distance_in_km":"8650.8","job_confirmed":3},{"userId":"94","name":"dfasdf","profileImage":"http://dev.livewire.work/./uploads/profile/default.png","category_name":"Transport","parent_category":"family","max_rate":"500","min_rate":"450","distance_in_km":"26.1","job_confirmed":3}]
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
         * userId : 1
         * name : tushar
         * profileImage : http://dev.livewire.work/./uploads/profile/thumb/yAjRdCMZbofgOQu4.jpg
         * category_name : Transport
         * parent_category : family
         * max_rate : 500
         * min_rate : 450
         * distance_in_km : 125.9
         * job_confirmed : 3
         */

        private String userId;
        private String name;
        private String profileImage;
        private String category_name;
        private String parent_category;
        private String max_rate;
        private String min_rate;
        private String distance_in_km;
        private int job_confirmed;

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

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public String getParent_category() {
            return parent_category;
        }

        public void setParent_category(String parent_category) {
            this.parent_category = parent_category;
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

        public String getDistance_in_km() {
            return distance_in_km;
        }

        public void setDistance_in_km(String distance_in_km) {
            this.distance_in_km = distance_in_km;
        }

        public int getJob_confirmed() {
            return job_confirmed;
        }

        public void setJob_confirmed(int job_confirmed) {
            this.job_confirmed = job_confirmed;
        }
    }
}
