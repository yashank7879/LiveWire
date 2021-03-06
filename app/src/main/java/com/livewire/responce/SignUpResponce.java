package com.livewire.responce;

import java.io.Serializable;

/**
 * Created by mindiii on 9/17/18.
 */

public class SignUpResponce implements Serializable {
    /**
     * status : success
     * message : Registration successfully done
     *  "STRIPE_FEES": "2.9",
     "STRIPE_TRANSACTION_FEES": "0.30"
     * data : {"userId":"22","name":"yash","email":"yash1@gmail.com",
     * "password":"$2y$10$Jk3DURGnpSOlJbqxjH0Urerv4uDAQGSIZDMITcj8MRNbFEXHSE0Qi",
     * "userType":"worker","firebase_id":"","Is_notify":"1","completeProfile":"0",
     * "deviceType":"2","deviceToken":"","socialId":"","socialType":"",
     * "authToken":"cfa64283c3b9bfe2ae45cd557138075abf21c4d8","status":"1",
     * "crd":"2018-09-17 12:14:01","upd":"0000-00-00 00:00:00"
     * }
     */

    private String status;
    private String message;
    private String stripe_fees;
    private String stripe_transaction_fees;

    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getStripe_fees() {
        return stripe_fees;
    }

    public void setStripe_fees(String stripe_fees) {
        this.stripe_fees = stripe_fees;
    }

    public String getStripe_transaction_fees() {
        return stripe_transaction_fees;
    }

    public void setStripe_transaction_fees(String stripe_transaction_fees) {
        this.stripe_transaction_fees = stripe_transaction_fees;
    }

    public static class DataBean implements Serializable{
        /**
         * userId : 22
         * name : yash
         * email : yash1@gmail.com
         * password : $2y$10$Jk3DURGnpSOlJbqxjH0Urerv4uDAQGSIZDMITcj8MRNbFEXHSE0Qi
         * userType : worker
         * firebase_id :
         * Is_notify : 1
         * completeProfile : 0
         * deviceType : 2
         * deviceToken :
         * socialId :
         * socialType :
         * authToken : cfa64283c3b9bfe2ae45cd557138075abf21c4d8
         * status : 1
         * crd : 2018-09-17 12:14:01
         * upd : 0000-00-00 00:00:00
         *confirm_dob :"0"
         *availability :"0"
         */

        private String userId = "";
        private String name = "";
        private String email = "";
        private String password = "";
        private String userType = "";
        private String firebase_id = "";
        private String Is_notify = "";
        private String completeProfile = "";
        private String deviceType = "";
        private String deviceToken = "";
        private String socialId = "";
        private String socialType = "";
        private String authToken = "";
        private String status = "";
        private String crd = "";
        private String upd = "";
        private String user_intro_vodeo = "";
        private String thumbImage = "";
        private String town = "";
        private String totalIncome ="";
        private String profileImage ="";
        private String latitude ="";
        private String longitude ="";
        private String is_bank_account ="";
        private String stripe_customer_id ="";
        private String rating ="";
        private String complete_job ="";
        private String app_user_mode ="";
        private String confirm_dob ="";
        private String availability ="";


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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getFirebase_id() {
            return firebase_id;
        }

        public void setFirebase_id(String firebase_id) {
            this.firebase_id = firebase_id;
        }

        public String getIs_notify() {
            return Is_notify;
        }

        public void setIs_notify(String Is_notify) {
            this.Is_notify = Is_notify;
        }

        public String getCompleteProfile() {
            return completeProfile;
        }

        public void setCompleteProfile(String completeProfile) {
            this.completeProfile = completeProfile;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getSocialId() {
            return socialId;
        }

        public void setSocialId(String socialId) {
            this.socialId = socialId;
        }

        public String getSocialType() {
            return socialType;
        }

        public void setSocialType(String socialType) {
            this.socialType = socialType;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCrd() {
            return crd;
        }

        public void setCrd(String crd) {
            this.crd = crd;
        }

        public String getUpd() {
            return upd;
        }

        public void setUpd(String upd) {
            this.upd = upd;
        }

        public String getUser_intro_vodeo() {
            return user_intro_vodeo;
        }

        public void setUser_intro_vodeo(String user_intro_vodeo) {
            this.user_intro_vodeo = user_intro_vodeo;
        }

        public String getThumbImage() {
            return thumbImage;
        }

        public void setThumbImage(String thumbImage) {
            this.thumbImage = thumbImage;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getTotalIncome() {
            return totalIncome;
        }

        public void setTotalIncome(String totalIncome) {
            this.totalIncome = totalIncome;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

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

        public String getIs_bank_account() {
            return is_bank_account;
        }

        public void setIs_bank_account(String is_bank_account) {
            this.is_bank_account = is_bank_account;
        }

        public String getStripe_customer_id() {
            return stripe_customer_id;
        }

        public void setStripe_customer_id(String stripe_customer_id) {
            this.stripe_customer_id = stripe_customer_id;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getComplete_job() {
            return complete_job;
        }

        public void setComplete_job(String complete_job) {
            this.complete_job = complete_job;
        }

        public String getUser_mode() {
            return app_user_mode;
        }

        public void setUser_mode(String user_mode) {
            this.app_user_mode = user_mode;
        }

        public String getConfirm_dob() {
            return confirm_dob;
        }

        public void setConfirm_dob(String confirm_dob) {
            this.confirm_dob = confirm_dob;
        }

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }
    }
}
