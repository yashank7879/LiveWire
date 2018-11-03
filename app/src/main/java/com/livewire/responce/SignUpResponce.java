package com.livewire.responce;

/**
 * Created by mindiii on 9/17/18.
 */

public class SignUpResponce {
    /**
     * status : success
     * message : Registration successfully done
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

    public static class DataBean {
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
    }
}
