package com.livewire.responce;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 2/27/19.
 */

public class NearByResponce implements Serializable{

    /**
     * status : success
     * message : OK
     * data : [{"userId":"2","name":"tushar","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/jeXiPE7rg0NfBmoW.jpg","distance_in_km":"2.7","http":"jeXi","https":"jeXiP","rating":""},{"userId":"4","name":"client","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/dXleRtkgFK3124Qo.jpg","distance_in_km":"3.4","http":"dXle","https":"dXleR","rating":""},{"userId":"5","name":"worker","profileImage":"http://dev.livewire.work/./uploads/profile/default.png","distance_in_km":"3.4","http":"","https":"","rating":""},{"userId":"6","name":"admin","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/cvXNkOyJHDbMEt2P.jpg","distance_in_km":"2.2","http":"cvXN","https":"cvXNk","rating":""},{"userId":"10","name":"aks1111111","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/suviVGcbXrLjSqeP.jpeg","distance_in_km":"2.7","http":"suvi","https":"suviV","rating":""},{"userId":"14","name":"Manish Pareek","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/bZPeURvj5GnC6AKk.jpg","distance_in_km":"0.0","http":"bZPe","https":"bZPeU","rating":""},{"userId":"18","name":"Rohit","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/DGaMurX1H7jWLCwx.jpeg","distance_in_km":"0.0","http":"DGaM","https":"DGaMu","rating":""},{"userId":"21","name":"jerry","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/L69UzvmaJXPqWl2s.jpg","distance_in_km":"2.7","http":"L69U","https":"L69Uz","rating":""},{"userId":"25","name":"Spider","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/wtTMv1CNyzkK7VgQ.jpeg","distance_in_km":"3.4","http":"wtTM","https":"wtTMv","rating":""},{"userId":"26","name":"Bella Swan","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/BxIAvJzOP7ZSMuFc.jpeg","distance_in_km":"2.7","http":"BxIA","https":"BxIAv","rating":""},{"userId":"29","name":"Shubham Verma","profileImage":"https://lh3.googleusercontent.com/a-/AAuE7mBp_i9_pPIWlxcgvVOE4w2fUwAEd8hZwwADsAZE","distance_in_km":"3.4","http":"http","https":"https","rating":""},{"userId":"30","name":"Prish","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/MJH4l1OiRxjBbhpg.jpeg","distance_in_km":"3.4","http":"MJH4","https":"MJH4l","rating":""},{"userId":"31","name":"Braj Sharma","profileImage":"http://dev.livewire.work/./uploads/profile/default.png","distance_in_km":"3.4","http":"","https":"","rating":""},{"userId":"33","name":"Jerry smith","profileImage":"https://lh3.googleusercontent.com/a-/AAuE7mCP_2cRa9vGcE7ZSkwCQXhFXAc1OSBVCoPzvtSW","distance_in_km":"3.0","http":"http","https":"https","rating":""}]
     * currentDateTime : 2019-02-27 06:09:44
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

    public static class DataBean implements Serializable {
        /**
         * userId : 2
         * name : tushar
         * profileImage : http://dev.livewire.work/./uploads/profile/thumb/jeXiPE7rg0NfBmoW.jpg
         * distance_in_km : 2.7
         * http : jeXi
         * https : jeXiP
         * rating :
         */

        private String userId;
        private String name;
        private String profileImage;
        private String distance_in_km;
        private String latitude;
        private String longitude;
        private String http;
        private String https;
        private String rating;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }



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
