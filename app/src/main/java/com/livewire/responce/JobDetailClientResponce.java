package com.livewire.responce;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 11/21/18.
 */

public class JobDetailClientResponce implements Serializable {

    /**
     * status : success
     * message : OK
     * data : {"jobId":"178","category_id":"7","job_start_date":"2018-11-19","job_end_date":"0000-00-00","job_budget":"33","job_location":"Chicago, IL, USA","job_latitude":"41.8781136","job_longitude":"-87.6297982","job_type":"1","job_description":"years ago and I have a few questions about the door and I will be there at noon","crd":"2018-11-19 06:34:38","job_week_days":"","job_time_duration":"","parent_category":"Family","sub_category":"Babysitting","job_offer":"","total_request":"1","job_confirmed":"0","requestedUserData":[{"userId":"135","userType":"worker","name":"yashWorker","profileImage":"https://livewire.work/./uploads/profile/thumb/fUILw9c3PJdlkpZ5.jpg","request_status":"0","distance_in_km":"0.0","max_rate":"10","min_rate":"5"}],"currentDateTime":"2018-11-21 07:39:54"}
     */

    private String status;
    private String message;
    private DataBean data;
    private List<ReviewBean> review;

    public List<ReviewBean> getReview() {
        return review;
    }

    public void setReview(List<ReviewBean> review) {
        this.review = review;
    }

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

    public static class DataBean implements Serializable {
        /**
         * jobId : 178
         * category_id : 7
         * parent_category_id : 1
         * job_start_date : 2018-11-19
         * job_end_date : 0000-00-00
         * job_budget : 33
         * job_location : Chicago, IL, USA
         * job_latitude : 41.8781136
         * job_longitude : -87.6297982
         * job_type : 1
         * job_description : years ago and I have a few questions about the door and I will be there at noon
         * crd : 2018-11-19 06:34:38
         * job_week_days :
         * job_time_duration :
         * parent_category : Family
         * sub_category : Babysitting
         * job_offer :
         * total_request : 1
         * job_confirmed : 0
         * requestedUserData : [{"userId":"135","userType":"worker","name":"yashWorker","profileImage":"https://livewire.work/./uploads/profile/thumb/fUILw9c3PJdlkpZ5.jpg","request_status":"0","distance_in_km":"0.0","max_rate":"10","min_rate":"5"}]
         * currentDateTime : 2018-11-21 07:39:54
         */

        private String jobId;
        private String category_id;
        private String parent_category_id;
        private String job_start_date;
        private String job_end_date;
        private String job_budget;
        private String job_location;
        private String job_latitude;
        private String job_longitude;
        private String job_type;
        private String job_description;
        private String crd;
        private String job_week_days;
        private String job_time_duration;
        private String parent_category;
        private String sub_category;
        private String job_offer;
        private String total_request;
        private String job_confirmed;
        private String currentDateTime;
        private String number_of_days;


        private List<RequestedUserDataBean> requestedUserData;

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

        public String getJob_start_date() {
            return job_start_date;
        }

        public void setJob_start_date(String job_start_date) {
            this.job_start_date = job_start_date;
        }

        public String getJob_end_date() {
            return job_end_date;
        }

        public void setJob_end_date(String job_end_date) {
            this.job_end_date = job_end_date;
        }

        public String getJob_budget() {
            return job_budget;
        }

        public void setJob_budget(String job_budget) {
            this.job_budget = job_budget;
        }

        public String getJob_location() {
            return job_location;
        }

        public void setJob_location(String job_location) {
            this.job_location = job_location;
        }

        public String getJob_latitude() {
            return job_latitude;
        }

        public void setJob_latitude(String job_latitude) {
            this.job_latitude = job_latitude;
        }

        public String getJob_longitude() {
            return job_longitude;
        }

        public void setJob_longitude(String job_longitude) {
            this.job_longitude = job_longitude;
        }

        public String getJob_type() {
            return job_type;
        }

        public void setJob_type(String job_type) {
            this.job_type = job_type;
        }

        public String getJob_description() {
            return job_description;
        }

        public void setJob_description(String job_description) {
            this.job_description = job_description;
        }

        public String getCrd() {
            return crd;
        }

        public void setCrd(String crd) {
            this.crd = crd;
        }

        public String getJob_week_days() {
            return job_week_days;
        }

        public void setJob_week_days(String job_week_days) {
            this.job_week_days = job_week_days;
        }

        public String getJob_time_duration() {
            return job_time_duration;
        }

        public void setJob_time_duration(String job_time_duration) {
            this.job_time_duration = job_time_duration;
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

        public String getJob_offer() {
            return job_offer;
        }

        public void setJob_offer(String job_offer) {
            this.job_offer = job_offer;
        }

        public String getTotal_request() {
            return total_request;
        }

        public void setTotal_request(String total_request) {
            this.total_request = total_request;
        }

        public String getJob_confirmed() {
            return job_confirmed;
        }

        public void setJob_confirmed(String job_confirmed) {
            this.job_confirmed = job_confirmed;
        }

        public String getCurrentDateTime() {
            return currentDateTime;
        }

        public void setCurrentDateTime(String currentDateTime) {
            this.currentDateTime = currentDateTime;
        }

        public List<RequestedUserDataBean> getRequestedUserData() {
            return requestedUserData;
        }

        public void setRequestedUserData(List<RequestedUserDataBean> requestedUserData) {
            this.requestedUserData = requestedUserData;
        }

        public String getNumber_of_days() {
            return number_of_days;
        }

        public void setNumber_of_days(String number_of_days) {
            this.number_of_days = number_of_days;
        }

        public String getParent_category_id() {
            return parent_category_id;
        }

        public void setParent_category_id(String parent_category_id) {
            this.parent_category_id = parent_category_id;
        }

        public static class RequestedUserDataBean implements Serializable {
            /**
             * userId : 135
             * userType : worker
             * name : yashWorker
             * profileImage : https://livewire.work/./uploads/profile/thumb/fUILw9c3PJdlkpZ5.jpg
             * request_status : 0
             * distance_in_km : 0.0
             * max_rate : 10
             * min_rate : 5
             * review_status:1
             */

            private String userId;
            private String userType;
            private String name;
            private String profileImage;
            private String request_status;
            private String distance_in_km;
            private String max_rate;
            private String min_rate;
            private String rating="";
            private String review_status;

            public String getRating() {
                return rating;
            }

            public void setRating(String rating) {
                this.rating = rating;
            }
            public String getReview_status() {
                return review_status;
            }

            public void setReview_status(String review_status) {
                this.review_status = review_status;
            }



            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUserType() {
                return userType;
            }

            public void setUserType(String userType) {
                this.userType = userType;
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

            public String getRequest_status() {
                return request_status;
            }

            public void setRequest_status(String request_status) {
                this.request_status = request_status;
            }

            public String getDistance_in_km() {
                return distance_in_km;
            }

            public void setDistance_in_km(String distance_in_km) {
                this.distance_in_km = distance_in_km;
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
    public static class ReviewBean{
        private String review_by;
        private String rating;
        private String job_id;
        private String review_description;
        private String crd;

        public String getReview_by() {
            return review_by;
        }

        public void setReview_by(String review_by) {
            this.review_by = review_by;
        }

        public String getReview_for() {
            return review_for;
        }

        public void setReview_for(String review_for) {
            this.review_for = review_for;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getJob_id() {
            return job_id;
        }

        public void setJob_id(String job_id) {
            this.job_id = job_id;
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

        private String review_for;

    }
}
