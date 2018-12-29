package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 10/8/18.
 */

public class JobDetailWorkerResponce {

    /**
     * status : success
     * message : OK
     * data : {"userId":"121","name":"viss123","profileImage":"https://livewire.work/./uploads/profile/thumb/iVjgR4pMxPqcIzAK.jpg","jobId":"207","category_id":"7","job_start_date":"2018-11-20","job_end_date":"2018-12-29","job_offer":"55","job_location":"Chicago, IL, USA","job_latitude":"41.8781136","job_longitude":"-87.6297982","job_type":"2","job_description":"I have to go to the store and get some rest and feel better soon and that","crd":"2018-11-20 12:51:21","jobmetaId":"94","job_week_days":"Monday,Wednesday,Friday,Sunday,","job_time_duration":"5","parentCategoryName":"Family","subCategoryName":"Babysitting","distance_in_km":"3784.2","job_confirmed":"0","min_rate":"50","max_rate":"100"}
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

    public static class DataBean {
        /**
         * userId : 121
         * name : viss123
         * profileImage : https://livewire.work/./uploads/profile/thumb/iVjgR4pMxPqcIzAK.jpg
         * jobId : 207
         * category_id : 7
         * job_start_date : 2018-11-20
         * job_end_date : 2018-12-29
         * job_offer : 55
         * job_location : Chicago, IL, USA
         * job_latitude : 41.8781136
         * job_longitude : -87.6297982
         * job_type : 2
         * job_description : I have to go to the store and get some rest and feel better soon and that
         * crd : 2018-11-20 12:51:21
         * jobmetaId : 94
         * job_week_days : Monday,Wednesday,Friday,Sunday,
         * job_time_duration : 5
         * parentCategoryName : Family
         * subCategoryName : Babysitting
         * distance_in_km : 3784.2
         * job_confirmed : 0
         * min_rate : 50
         * max_rate : 100
         * number_of_days : 10
         * review_status: 1
         *
         *  /* "review":[
         {
         "review_by":"226",
         "review_for":"225",
         "rating":"4",
         "job_id":"372",
         "review_description":"i like your services\n",
         "crd":"2018-12-13 12:54:08"
         }
         ]*/


        private String userId ="";
        private String name="";
        private String profileImage="";
        private String jobId="";
        private String category_id="";
        private String job_start_date="";
        private String job_end_date="";
        private String job_offer="";
        private String job_location="";
        private String job_latitude="";
        private String job_longitude="";
        private String job_type="";
        private String job_description="";
        private String crd="";
        private String jobmetaId="";
        private String job_week_days="";
        private String job_time_duration="";
        private String parentCategoryName="";
        private String subCategoryName="";
        private String distance_in_km="";
        private String job_confirmed="";
        private String min_rate="";
        private String max_rate="";
        private String currentDateTime="";
        private String job_budget="";
        private String number_of_days="";
        private String review_status="";
        private String rating="";

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

        public String getJob_offer() {
            return job_offer;
        }

        public void setJob_offer(String job_offer) {
            this.job_offer = job_offer;
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

        public String getJobmetaId() {
            return jobmetaId;
        }

        public void setJobmetaId(String jobmetaId) {
            this.jobmetaId = jobmetaId;
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

        public String getParentCategoryName() {
            return parentCategoryName;
        }

        public void setParentCategoryName(String parentCategoryName) {
            this.parentCategoryName = parentCategoryName;
        }

        public String getSubCategoryName() {
            return subCategoryName;
        }

        public void setSubCategoryName(String subCategoryName) {
            this.subCategoryName = subCategoryName;
        }

        public String getDistance_in_km() {
            return distance_in_km;
        }

        public void setDistance_in_km(String distance_in_km) {
            this.distance_in_km = distance_in_km;
        }

        public String getJob_confirmed() {
            return job_confirmed;
        }

        public void setJob_confirmed(String job_confirmed) {
            this.job_confirmed = job_confirmed;
        }

        public String getMin_rate() {
            return min_rate;
        }

        public void setMin_rate(String min_rate) {
            this.min_rate = min_rate;
        }

        public String getMax_rate() {
            return max_rate;
        }

        public void setMax_rate(String max_rate) {
            this.max_rate = max_rate;
        }

        public String getCurrentDateTime() {
            return currentDateTime;
        }

        public void setCurrentDateTime(String currentDateTime) {
            this.currentDateTime = currentDateTime;
        }

        public String getJob_budget() {
            return job_budget;
        }

        public void setJob_budget(String job_budget) {
            this.job_budget = job_budget;
        }

        public String getNumber_of_days() {
            return number_of_days;
        }

        public void setNumber_of_days(String number_of_days) {
            this.number_of_days = number_of_days;
        }

        public String getReview_status() {
            return review_status;
        }

        public void setReview_status(String review_status) {
            this.review_status = review_status;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
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
