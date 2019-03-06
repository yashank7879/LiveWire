package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 2/21/19.
 */

public class NearYouModel {
    /**
     * status : success
     * message : OK
     * data : [{"userId":"4","name":"client","profileImage":"http://dev.livewire.work/./uploads/profile/default.png","distance_in_km":"3.4","http":"","https":"","rating":""},{"userId":"5","name":"worker","profileImage":"http://dev.livewire.work/./uploads/profile/default.png","distance_in_km":"3.4","http":"","https":"","rating":""},{"userId":"6","name":"admin","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/cvXNkOyJHDbMEt2P.jpg","distance_in_km":"2.2","http":"cvXN","https":"cvXNk","rating":""},{"userId":"10","name":"aks1111111","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/suviVGcbXrLjSqeP.jpeg","distance_in_km":"2.7","http":"suvi","https":"suviV","rating":""},{"userId":"14","name":"Manish Pareek","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/bZPeURvj5GnC6AKk.jpg","distance_in_km":"0.0","http":"bZPe","https":"bZPeU","rating":""},{"userId":"18","name":"Rohit","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/ulhwTxarUzmOobA4.jpeg","distance_in_km":"0.0","http":"ulhw","https":"ulhwT","rating":""}]
     * currentDateTime : 2019-02-21 11:18:50
     */

    private String status;
    private String message;
    private String currentDateTime;
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

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * userId : 4
         * name : client
         * profileImage : http://dev.livewire.work/./uploads/profile/default.png
         * distance_in_km : 3.4
         * http :
         * https :
         * rating :
         */

        private String userId;
        private String name;
        private String profileImage;
        private String distance_in_km;
        private String http;
        private String https;
        private String rating;

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

        public String getDistance_in_km() {
            return distance_in_km;
        }

        public void setDistance_in_km(String distance_in_km) {
            this.distance_in_km = distance_in_km;
        }

        public String getHttp() {
            return http;
        }

        public void setHttp(String http) {
            this.http = http;
        }

        public String getHttps() {
            return https;
        }

        public void setHttps(String https) {
            this.https = https;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }
}
