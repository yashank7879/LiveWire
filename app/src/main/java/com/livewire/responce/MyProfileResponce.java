package com.livewire.responce;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 11/1/18.
 */

public class MyProfileResponce implements Serializable {
    /**
     * status : success
     * message : Ok
     * data : {"userId":"3","name":"test","email":"test@gmail.com","password":"$2y$10$iDbHU2EzXREH2wZot6dTgOIZsPnOSbXbDEECQsw8OItsGQpI5DdDK","profileImage":"https://livewire.work/./uploads/profile/thumb/o8P0RY1TWVOhuxbJ.jpeg","dob":"0000-00-00","confirm_dob":"1","intro_video":"https://livewire.work/./uploads/introVideo/kR9CJqecwoNLVd42.mp4","video_thumb":"https://livewire.work/./uploads/video_thumb/thumb/zewhuKLv6Oc10BF3.png","intro_discription":"nothing","profile_address":"Indore,Madhya Pradesh,India","address_country":"India","address_state":"Madhya Pradesh","address_city":"Indore","address_latitude":"22.70489859875548","address_longitude":"75.90900220123854","profile_current_address":"Indore,Madhya Pradesh,India","current_country":"India","current_state":"Madhya Pradesh","current_city":"Indore","current_latitude":"22.70489859875548","current_longitude":"75.90900220123854","userType":"","user_mode":"client","app_user_mode":"client","firebase_id":"","Is_notify":"1","completeProfile":"1","deviceType":"1","deviceToken":"","socialId":"","socialType":"","authToken":"b2f68000567e8203407917f3f861e54c3f94b80e","stripe_customer_id":"","availability":"1","status":"1","crd":"2019-04-19 06:02:08","upd":"2019-05-14 11:24:41","rating":"3.111111111111111","job_created":"1350","job_completed_by_client":"306","total_expense":"261,110.00","total_income":{"total_income":"541,505.41","job_completed":"6"},"category":[{"parentCategoryId":"1","categoryName":"Family Wellbeing","parent_id":"0","subcat":[{"categoryId":"8","categoryName":"Tuition / Extra lessons","parent_id":"1","max_rate":"0","min_rate":"34"},{"categoryId":"9","categoryName":"Transport","parent_id":"1","max_rate":"0","min_rate":"456"},{"categoryId":"10","categoryName":"Counselling","parent_id":"1","max_rate":"0","min_rate":"33"},{"categoryId":"41","categoryName":"Mentoring & Personal Development","parent_id":"1","max_rate":"0","min_rate":"56"},{"categoryId":"7","categoryName":"Child Minding / Babysitting","parent_id":"1","max_rate":"0","min_rate":"3"}]},{"parentCategoryId":"2","categoryName":"Home & Garden","parent_id":"0","subcat":[{"categoryId":"12","categoryName":"Gardening","parent_id":"2","max_rate":"0","min_rate":"33"},{"categoryId":"11","categoryName":"DIY / home maintenance","parent_id":"2","max_rate":"0","min_rate":"45"},{"categoryId":"13","categoryName":"Cooking / Baking","parent_id":"2","max_rate":"0","min_rate":"65"},{"categoryId":"14","categoryName":"Pet Care","parent_id":"2","max_rate":"0","min_rate":"45"},{"categoryId":"15","categoryName":"Sewing / Knitting","parent_id":"2","max_rate":"0","min_rate":"35"},{"categoryId":"16","categoryName":"Decorating","parent_id":"2","max_rate":"0","min_rate":"76"},{"categoryId":"36","categoryName":"Cleaning & House Organization","parent_id":"2","max_rate":"0","min_rate":"56"}]},{"parentCategoryId":"3","categoryName":"Entertainment & Parties","parent_id":"0","subcat":[{"categoryId":"17","categoryName":"Travel Companion","parent_id":"3","max_rate":"0","min_rate":"65"},{"categoryId":"18","categoryName":"Transport","parent_id":"3","max_rate":"0","min_rate":"78"},{"categoryId":"33","categoryName":"Party and Function Co-ordination","parent_id":"3","max_rate":"0","min_rate":"76"},{"categoryId":"52","categoryName":"Catering","parent_id":"3","max_rate":"0","min_rate":"555"}]},{"parentCategoryId":"4","categoryName":"Administrative / Clerical","parent_id":"0","subcat":[{"categoryId":"19","categoryName":"Telephonic","parent_id":"4","max_rate":"0","min_rate":"767"},{"categoryId":"20","categoryName":"Clerical","parent_id":"4","max_rate":"0","min_rate":"987"},{"categoryId":"53","categoryName":"Computers & Information Technology","parent_id":"4","max_rate":"0","min_rate":"1258"}]},{"parentCategoryId":"54","categoryName":"Travel & Hospitality","parent_id":"0","subcat":[{"categoryId":"55","categoryName":"Restaurant / Guest House Assistance","parent_id":"54","max_rate":"0","min_rate":"234"}]}]}
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
         * userId : 3
         * name : test
         * email : test@gmail.com
         * password : $2y$10$iDbHU2EzXREH2wZot6dTgOIZsPnOSbXbDEECQsw8OItsGQpI5DdDK
         * profileImage : https://livewire.work/./uploads/profile/thumb/o8P0RY1TWVOhuxbJ.jpeg
         * dob : 0000-00-00
         * confirm_dob : 1
         * intro_video : https://livewire.work/./uploads/introVideo/kR9CJqecwoNLVd42.mp4
         * video_thumb : https://livewire.work/./uploads/video_thumb/thumb/zewhuKLv6Oc10BF3.png
         * intro_discription : nothing
         * profile_address : Indore,Madhya Pradesh,India
         * address_country : India
         * address_state : Madhya Pradesh
         * address_city : Indore
         * address_latitude : 22.70489859875548
         * address_longitude : 75.90900220123854
         * profile_current_address : Indore,Madhya Pradesh,India
         * current_country : India
         * current_state : Madhya Pradesh
         * current_city : Indore
         * current_latitude : 22.70489859875548
         * current_longitude : 75.90900220123854
         * userType :
         * user_mode : client
         * app_user_mode : client
         * firebase_id :
         * Is_notify : 1
         * completeProfile : 1
         * deviceType : 1
         * deviceToken :
         * socialId :
         * socialType :
         * authToken : b2f68000567e8203407917f3f861e54c3f94b80e
         * stripe_customer_id :
         * availability : 1
         * status : 1
         * crd : 2019-04-19 06:02:08
         * upd : 2019-05-14 11:24:41
         * rating : 3.111111111111111
         * job_created : 1350
         * job_completed_by_client : 306
         * total_expense : 261,110.00
         * total_income : {"total_income":"541,505.41","job_completed":"6"}
         * category : [{"parentCategoryId":"1","categoryName":"Family Wellbeing","parent_id":"0","subcat":[{"categoryId":"8","categoryName":"Tuition / Extra lessons","parent_id":"1","max_rate":"0","min_rate":"34"},{"categoryId":"9","categoryName":"Transport","parent_id":"1","max_rate":"0","min_rate":"456"},{"categoryId":"10","categoryName":"Counselling","parent_id":"1","max_rate":"0","min_rate":"33"},{"categoryId":"41","categoryName":"Mentoring & Personal Development","parent_id":"1","max_rate":"0","min_rate":"56"},{"categoryId":"7","categoryName":"Child Minding / Babysitting","parent_id":"1","max_rate":"0","min_rate":"3"}]},{"parentCategoryId":"2","categoryName":"Home & Garden","parent_id":"0","subcat":[{"categoryId":"12","categoryName":"Gardening","parent_id":"2","max_rate":"0","min_rate":"33"},{"categoryId":"11","categoryName":"DIY / home maintenance","parent_id":"2","max_rate":"0","min_rate":"45"},{"categoryId":"13","categoryName":"Cooking / Baking","parent_id":"2","max_rate":"0","min_rate":"65"},{"categoryId":"14","categoryName":"Pet Care","parent_id":"2","max_rate":"0","min_rate":"45"},{"categoryId":"15","categoryName":"Sewing / Knitting","parent_id":"2","max_rate":"0","min_rate":"35"},{"categoryId":"16","categoryName":"Decorating","parent_id":"2","max_rate":"0","min_rate":"76"},{"categoryId":"36","categoryName":"Cleaning & House Organization","parent_id":"2","max_rate":"0","min_rate":"56"}]},{"parentCategoryId":"3","categoryName":"Entertainment & Parties","parent_id":"0","subcat":[{"categoryId":"17","categoryName":"Travel Companion","parent_id":"3","max_rate":"0","min_rate":"65"},{"categoryId":"18","categoryName":"Transport","parent_id":"3","max_rate":"0","min_rate":"78"},{"categoryId":"33","categoryName":"Party and Function Co-ordination","parent_id":"3","max_rate":"0","min_rate":"76"},{"categoryId":"52","categoryName":"Catering","parent_id":"3","max_rate":"0","min_rate":"555"}]},{"parentCategoryId":"4","categoryName":"Administrative / Clerical","parent_id":"0","subcat":[{"categoryId":"19","categoryName":"Telephonic","parent_id":"4","max_rate":"0","min_rate":"767"},{"categoryId":"20","categoryName":"Clerical","parent_id":"4","max_rate":"0","min_rate":"987"},{"categoryId":"53","categoryName":"Computers & Information Technology","parent_id":"4","max_rate":"0","min_rate":"1258"}]},{"parentCategoryId":"54","categoryName":"Travel & Hospitality","parent_id":"0","subcat":[{"categoryId":"55","categoryName":"Restaurant / Guest House Assistance","parent_id":"54","max_rate":"0","min_rate":"234"}]}]
         */

        private String userId;
        private String name;
        private String email;
        private String password;
        private String profileImage;
        private String dob;
        private String confirm_dob;
        private String intro_video;
        private String video_thumb;
        private String intro_discription;
        private String profile_address;
        private String address_country;
        private String address_state;
        private String address_city;
        private String address_latitude;
        private String address_longitude;
        private String profile_current_address;
        private String current_country;
        private String current_state;
        private String current_city;
        private String current_latitude;
        private String current_longitude;
        private String userType;
        private String user_mode;
        private String app_user_mode;
        private String firebase_id;
        private String Is_notify;
        private String completeProfile;
        private String deviceType;
        private String deviceToken;
        private String socialId;
        private String socialType;
        private String authToken;
        private String stripe_customer_id;
        private String availability;
        private String status;
        private String crd;
        private String upd;
        private String rating;
        private String job_created;
        private String job_completed_by_client;
        private String total_expense;
        private TotalIncomeBean total_income;
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

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getConfirm_dob() {
            return confirm_dob;
        }

        public void setConfirm_dob(String confirm_dob) {
            this.confirm_dob = confirm_dob;
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

        public String getAddress_country() {
            return address_country;
        }

        public void setAddress_country(String address_country) {
            this.address_country = address_country;
        }

        public String getAddress_state() {
            return address_state;
        }

        public void setAddress_state(String address_state) {
            this.address_state = address_state;
        }

        public String getAddress_city() {
            return address_city;
        }

        public void setAddress_city(String address_city) {
            this.address_city = address_city;
        }

        public String getAddress_latitude() {
            return address_latitude;
        }

        public void setAddress_latitude(String address_latitude) {
            this.address_latitude = address_latitude;
        }

        public String getAddress_longitude() {
            return address_longitude;
        }

        public void setAddress_longitude(String address_longitude) {
            this.address_longitude = address_longitude;
        }

        public String getProfile_current_address() {
            return profile_current_address;
        }

        public void setProfile_current_address(String profile_current_address) {
            this.profile_current_address = profile_current_address;
        }

        public String getCurrent_country() {
            return current_country;
        }

        public void setCurrent_country(String current_country) {
            this.current_country = current_country;
        }

        public String getCurrent_state() {
            return current_state;
        }

        public void setCurrent_state(String current_state) {
            this.current_state = current_state;
        }

        public String getCurrent_city() {
            return current_city;
        }

        public void setCurrent_city(String current_city) {
            this.current_city = current_city;
        }

        public String getCurrent_latitude() {
            return current_latitude;
        }

        public void setCurrent_latitude(String current_latitude) {
            this.current_latitude = current_latitude;
        }

        public String getCurrent_longitude() {
            return current_longitude;
        }

        public void setCurrent_longitude(String current_longitude) {
            this.current_longitude = current_longitude;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getUser_mode() {
            return user_mode;
        }

        public void setUser_mode(String user_mode) {
            this.user_mode = user_mode;
        }

        public String getApp_user_mode() {
            return app_user_mode;
        }

        public void setApp_user_mode(String app_user_mode) {
            this.app_user_mode = app_user_mode;
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

        public String getStripe_customer_id() {
            return stripe_customer_id;
        }

        public void setStripe_customer_id(String stripe_customer_id) {
            this.stripe_customer_id = stripe_customer_id;
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

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getJob_created() {
            return job_created;
        }

        public void setJob_created(String job_created) {
            this.job_created = job_created;
        }

        public String getJob_completed_by_client() {
            return job_completed_by_client;
        }

        public void setJob_completed_by_client(String job_completed_by_client) {
            this.job_completed_by_client = job_completed_by_client;
        }

        public String getTotal_expense() {
            return total_expense;
        }

        public void setTotal_expense(String total_expense) {
            this.total_expense = total_expense;
        }

        public TotalIncomeBean getTotal_income() {
            return total_income;
        }

        public void setTotal_income(TotalIncomeBean total_income) {
            this.total_income = total_income;
        }

        public List<CategoryBean> getCategory() {
            return category;
        }

        public void setCategory(List<CategoryBean> category) {
            this.category = category;
        }

        public static class TotalIncomeBean implements Serializable {
            /**
             * total_income : 541,505.41
             * job_completed : 6
             */

            private String total_income;
            private String job_completed;

            public String getTotal_income() {
                return total_income;
            }

            public void setTotal_income(String total_income) {
                this.total_income = total_income;
            }

            public String getJob_completed() {
                return job_completed;
            }

            public void setJob_completed(String job_completed) {
                this.job_completed = job_completed;
            }
        }

        public static class CategoryBean implements Serializable {
            /**
             * parentCategoryId : 1
             * categoryName : Family Wellbeing
             * parent_id : 0
             * subcat : [{"categoryId":"8","categoryName":"Tuition / Extra lessons","parent_id":"1","max_rate":"0","min_rate":"34"},{"categoryId":"9","categoryName":"Transport","parent_id":"1","max_rate":"0","min_rate":"456"},{"categoryId":"10","categoryName":"Counselling","parent_id":"1","max_rate":"0","min_rate":"33"},{"categoryId":"41","categoryName":"Mentoring & Personal Development","parent_id":"1","max_rate":"0","min_rate":"56"},{"categoryId":"7","categoryName":"Child Minding / Babysitting","parent_id":"1","max_rate":"0","min_rate":"3"}]
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
                 * categoryId : 8
                 * categoryName : Tuition / Extra lessons
                 * parent_id : 1
                 * max_rate : 0
                 * min_rate : 34
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
