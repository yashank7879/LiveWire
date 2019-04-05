package com.livewire.responce;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 11/1/18.
 */

public class MyProfileResponce implements Serializable {

    /**
     * status : success
     * message : OK
     * data : {"userId":"27","name":"purva","email":"purva@gmail.com","password":"$2y$10$aOPg40NLDgzQ1tRTrv7DaeMJzvcNtB1/rSpDrZO2HCLCkJBtVvLUq","profileImage":"","intro_video":"CcOiVUsDn5brAXR8.3gp","video_thumb":"","town":"indore","latitude":"1.0215","longitude":"1.0256","userType":"worker","firebase_id":"","Is_notify":"1","completeProfile":"1","deviceType":"2","deviceToken":"","socialId":"","socialType":"","authToken":"bd9453e36bf063bee77c996a55ef0020fa3d500d","availability":"1","status":"1","crd":"2018-10-17 05:36:20","upd":"2018-10-29 15:13:55","category":[{"parentCategoryId":"1","categoryName":"Family","parent_id":"0","subcat":[{"categoryId":"7","categoryName":"Babysitting","parent_id":"1","max_rate":"100","min_rate":"50"},{"categoryId":"9","categoryName":"Transport","parent_id":"1","max_rate":"500","min_rate":"450"}]}],"user_intro_video":"http://dev.livewire.work/./uploads/introVideo/CcOiVUsDn5brAXR8.3gp","thumbImage":"http://dev.livewire.work/./uploads/profile/default.png"}
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

    public static class DataBean implements Serializable{
        /**
         * userId : 27
         * name : purva
         * email : purva@gmail.com
         * password : $2y$10$aOPg40NLDgzQ1tRTrv7DaeMJzvcNtB1/rSpDrZO2HCLCkJBtVvLUq
         * profileImage :
         * intro_video : CcOiVUsDn5brAXR8.3gp
         * video_thumb :
         * town : indore
         * latitude : 1.0215
         * longitude : 1.0256
         * userType : worker
         * firebase_id :
         * Is_notify : 1
         * completeProfile : 1
         * deviceType : 2
         * deviceToken :
         * socialId :
         * socialType :
         * authToken : bd9453e36bf063bee77c996a55ef0020fa3d500d
         * availability : 1
         * status : 1
         * crd : 2018-10-17 05:36:20
         * upd : 2018-10-29 15:13:55
         * category : [{"parentCategoryId":"1","categoryName":"Family","parent_id":"0","subcat":[{"categoryId":"7","categoryName":"Babysitting","parent_id":"1","max_rate":"100","min_rate":"50"},{"categoryId":"9","categoryName":"Transport","parent_id":"1","max_rate":"500","min_rate":"450"}]}]
         * user_intro_video : http://dev.livewire.work/./uploads/introVideo/CcOiVUsDn5brAXR8.3gp
         * thumbImage : http://dev.livewire.work/./uploads/profile/default.png
         */

        private String userId="";
        private String name="";
        private String email="";
        private String password="";
        private String profileImage="";
        private String intro_video="";
        private String video_thumb="";
        private String town="";
        private String address_latitude="";
        private String address_longitude="";
        private String userType="";
        private String firebase_id="";
        private String Is_notify="";
        private String completeProfile="";
        private String deviceType="";
        private String deviceToken="";
        private String socialId="";
        private String socialType="";
        private String authToken="";
        private String availability="";
        private String status="";
        private String crd="";
        private String upd="";
        private String user_intro_video="";
        private String thumbImage="";
        private String totalIncome="";
        private String rating="";
        private String intro_discription="";
        private String job_created="";
        private String complete_job="";
        private String profile_address="";

        public String getComplete_job() {
            return complete_job;
        }

        public void setComplete_job(String complete_job) {
            this.complete_job = complete_job;
        }

        public String getJob_created() {
            return job_created;
        }

        public void setJob_created(String job_created) {
            this.job_created = job_created;
        }


        private List<CategoryBean> category;

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

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getIntro_video() {
            return intro_video;
        }

        public void setIntro_video(String intro_video) {
            this.intro_video = intro_video;
        }

        public String getVideo_thumb() {
            return video_thumb;
        }

        public void setVideo_thumb(String video_thumb) {
            this.video_thumb = video_thumb;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getLatitude() {
            return address_latitude;
        }

        public void setLatitude(String address_latitude) {
            this.address_latitude = address_latitude;
        }

        public String getLongitude() {
            return address_longitude;
        }

        public void setLongitude(String address_longitude) {
            this.address_longitude = address_longitude;
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

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
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

        public String getUser_intro_video() {
            return user_intro_video;
        }

        public void setUser_intro_video(String user_intro_video) {
            this.user_intro_video = user_intro_video;
        }

        public String getThumbImage() {
            return thumbImage;
        }

        public void setThumbImage(String thumbImage) {
            this.thumbImage = thumbImage;
        }

        public List<CategoryBean> getCategory() {
            return category;
        }

        public void setCategory(List<CategoryBean> category) {
            this.category = category;
        }

        public String getTotalIncome() {
            return totalIncome;
        }

        public void setTotalIncome(String totalIncome) {
            this.totalIncome = totalIncome;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getIntro_discription() {
            return intro_discription;
        }

        public void setIntro_discription(String intro_discription) {
            this.intro_discription = intro_discription;
        }

        public String getProfile_address() {
            return profile_address;
        }

        public void setProfile_address(String profile_address) {
            this.profile_address = profile_address;
        }

        public static class CategoryBean implements Serializable{
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

            public static class SubcatBean implements Serializable{
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
    }
}
