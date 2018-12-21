package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 12/5/18.
 */

public class CompleteJobResponce {
    /**
     * status : success
     * message : OK
     * data : [{"jobId":"301","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"699","job_type":"1","request_status":"4","upd":"2018-12-05 09:36:40","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"302","category_id":"7","user_id":"194","job_start_date":"2018-12-08","job_budget":"88","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"303","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"60","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:58","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"305","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:04:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"306","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:33:11","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"307","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"888","job_type":"1","request_status":"4","upd":"2018-12-05 08:52:08","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"301","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"699","job_type":"1","request_status":"4","upd":"2018-12-05 09:36:40","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"302","category_id":"7","user_id":"194","job_start_date":"2018-12-08","job_budget":"88","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"303","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"60","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:58","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"305","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:04:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"306","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:33:11","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"307","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"888","job_type":"1","request_status":"4","upd":"2018-12-05 08:52:08","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"301","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"699","job_type":"1","request_status":"4","upd":"2018-12-05 09:36:40","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"302","category_id":"7","user_id":"194","job_start_date":"2018-12-08","job_budget":"88","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"303","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"60","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:58","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"305","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:04:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"306","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:33:11","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"307","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"888","job_type":"1","request_status":"4","upd":"2018-12-05 08:52:08","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"301","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"699","job_type":"1","request_status":"4","upd":"2018-12-05 09:36:40","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"302","category_id":"7","user_id":"194","job_start_date":"2018-12-08","job_budget":"88","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"303","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"60","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:58","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"305","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:04:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"306","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:33:11","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"307","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"888","job_type":"1","request_status":"4","upd":"2018-12-05 08:52:08","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"5"},{"jobId":"301","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"699","job_type":"1","request_status":"4","upd":"2018-12-05 09:36:40","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"3"},{"jobId":"302","category_id":"7","user_id":"194","job_start_date":"2018-12-08","job_budget":"88","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"3"},{"jobId":"303","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"60","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:58","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"3"},{"jobId":"305","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:04:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"3"},{"jobId":"306","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:33:11","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"3"},{"jobId":"307","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"888","job_type":"1","request_status":"4","upd":"2018-12-05 08:52:08","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"3"},{"jobId":"301","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"699","job_type":"1","request_status":"4","upd":"2018-12-05 09:36:40","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"302","category_id":"7","user_id":"194","job_start_date":"2018-12-08","job_budget":"88","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"303","category_id":"7","user_id":"194","job_start_date":"2018-12-29","job_budget":"60","job_type":"1","request_status":"4","upd":"2018-12-05 08:38:58","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"305","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:04:01","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"306","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"96","job_type":"1","request_status":"4","upd":"2018-12-05 09:33:11","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"},{"jobId":"307","category_id":"7","user_id":"194","job_start_date":"2018-12-05","job_budget":"888","job_type":"1","request_status":"4","upd":"2018-12-05 08:52:08","parent_category":"Family","sub_category":"Babysitting","userId":"159","name":"krishna","profileImage":"https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg","distance_in_km":"6096.9","rating":"4"}]
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
         * jobId : 301
         * category_id : 7
         * user_id : 194
         * job_start_date : 2018-12-29
         * job_budget : 699
         * job_type : 1
         * request_status : 4
         * upd : 2018-12-05 09:36:40
         * parent_category : Family
         * sub_category : Babysitting
         * userId : 159
         * name : krishna
         * profileImage : https://livewire.work/./uploads/profile/thumb/aru6yYUOQSPKXoHR.jpg
         * distance_in_km : 6096.9
         * rating : 5
         * "job_time_duration":"3",
         "number_of_days":"5",
         "job_offer":"55",
         */

        private String jobId;
        private String category_id;
        private String user_id;
        private String job_start_date;
        private String job_budget;
        private String job_type;
        private String request_status;
        private String upd;
        private String parent_category;
        private String sub_category;
        private String userId;
        private String name;
        private String profileImage;
        private String job_time_duration;
        private String number_of_days;
        private String job_offer;
        private String distance_in_km;
        private String rating;

        public String getJob_time_duration() {
            return job_time_duration;
        }

        public void setJob_time_duration(String job_time_duration) {
            this.job_time_duration = job_time_duration;
        }

        public String getNumber_of_days() {
            return number_of_days;
        }

        public void setNumber_of_days(String number_of_days) {
            this.number_of_days = number_of_days;
        }

        public String getJob_offer() {
            return job_offer;
        }

        public void setJob_offer(String job_offer) {
            this.job_offer = job_offer;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getJob_start_date() {
            return job_start_date;
        }

        public void setJob_start_date(String job_start_date) {
            this.job_start_date = job_start_date;
        }

        public String getJob_budget() {
            return job_budget;
        }

        public void setJob_budget(String job_budget) {
            this.job_budget = job_budget;
        }

        public String getJob_type() {
            return job_type;
        }

        public void setJob_type(String job_type) {
            this.job_type = job_type;
        }

        public String getRequest_status() {
            return request_status;
        }

        public void setRequest_status(String request_status) {
            this.request_status = request_status;
        }

        public String getUpd() {
            return upd;
        }

        public void setUpd(String upd) {
            this.upd = upd;
        }

        public String getParent_category() {
            return parent_category;
        }

        public void setParent_category(String parent_category) {
            this.parent_category = parent_category;
        }

        public String getSub_category() {
            return sub_category;
        }

        public void setSub_category(String sub_category) {
            this.sub_category = sub_category;
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

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }
}
