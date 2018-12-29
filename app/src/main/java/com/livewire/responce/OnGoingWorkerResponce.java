package com.livewire.responce;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 10/22/18.
 */

public class OnGoingWorkerResponce implements Serializable{
    /**
     * status : success
     * message : OK
     * data : [{"userId":"115","name":"suraj","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/tuIRBabHGm7lFKEc.jpg","jobId":"142","category_id":"8","job_start_date":"2018-10-17","job_end_date":"2018-12-14","job_offer_rate":"","job_location":"India","job_latitude":"20.593684000000003","job_longitude":"78.96288","job_description":"hdjhkh hdih","crd":"2018-10-17 04:18:35","jobmetaId":"1","job_week_days":"Monday,","job_time_duration":"5","parentCategoryName":"family","subCategoryName":"Tuition / Extra lessons","distance_in_km":"0.0","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"116","name":"Puja","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/5uGavw9rlF8L3hnM.jpeg","jobId":"157","category_id":"11","job_start_date":"2018-10-22","job_end_date":"2018-10-22","job_offer_rate":"","job_location":"Bhopal, Madhya Pradesh, India","job_latitude":"23.2599333","job_longitude":"77.412615","job_description":"Welcome","crd":"0000-00-00 00:00:00","jobmetaId":"9","job_week_days":"Sunday,Monday","job_time_duration":"2","parentCategoryName":"hous & home","subCategoryName":"DIY / home maintenance","distance_in_km":"209.3","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"115","name":"suraj","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/tuIRBabHGm7lFKEc.jpg","jobId":"152","category_id":"7","job_start_date":"2018-10-22","job_end_date":"2018-10-22","job_offer_rate":"","job_location":"462001, Pal Tower, Sivoy, Gulmohar Colony, East Railway Colony, Bhopal, Madhya Pradesh 462039, India","job_latitude":"23.2677851","job_longitude":"77.4140094","job_description":"Welcome","crd":"0000-00-00 00:00:00","jobmetaId":"4","job_week_days":"Saturday,Thursday","job_time_duration":"3","parentCategoryName":"family","subCategoryName":"Babysitting","distance_in_km":"209.7","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"115","name":"suraj","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/tuIRBabHGm7lFKEc.jpg","jobId":"153","category_id":"8","job_start_date":"2018-10-22","job_end_date":"2018-10-22","job_offer_rate":"","job_location":"Bhopal Airport New Terminal Rd, Adarsh Colony, Gandhi Nagar, Bhopal, Madhya Pradesh 462030, India","job_latitude":"23.2963552","job_longitude":"77.338855","job_description":"Welcome","crd":"0000-00-00 00:00:00","jobmetaId":"5","job_week_days":"Friday,Thursday","job_time_duration":"3","parentCategoryName":"family","subCategoryName":"Tuition / Extra lessons","distance_in_km":"213.8","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"115","name":"suraj","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/tuIRBabHGm7lFKEc.jpg","jobId":"154","category_id":"11","job_start_date":"2018-10-22","job_end_date":"2018-10-22","job_offer_rate":"","job_location":"Chhoti Gwaltoli, Indore, Madhya Pradesh 452007, India","job_latitude":"22.717019","job_longitude":"75.8684371","job_description":"Welcome","crd":"0000-00-00 00:00:00","jobmetaId":"6","job_week_days":"Friday,Wednesday","job_time_duration":"5","parentCategoryName":"hous & home","subCategoryName":"DIY / home maintenance","distance_in_km":"247.0","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"115","name":"suraj","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/tuIRBabHGm7lFKEc.jpg","jobId":"155","category_id":"8","job_start_date":"2018-10-22","job_end_date":"2018-10-22","job_offer_rate":"","job_location":"Ujjjaina Rd, Ujjaina, Bihar, India","job_latitude":"26.0303829","job_longitude":"86.0364949","job_description":"Welcome","crd":"0000-00-00 00:00:00","jobmetaId":"7","job_week_days":"Wednesday,Thursday","job_time_duration":"5","parentCategoryName":"family","subCategoryName":"Tuition / Extra lessons","distance_in_km":"585.1","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"124","name":"Siya","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/PBcTnZ3v4miNjtdJ.jpeg","jobId":"156","category_id":"8","job_start_date":"2018-10-22","job_end_date":"2018-10-24","job_offer_rate":"","job_location":"Nyandarua District, Kenya","job_latitude":"-0.4915612","job_longitude":"36.6171528","job_description":"Ugh gh hg hg h h they hggj th uhh gh ghg gjjhjhh hjdfj hh jhbgjgjjhj h","crd":"0000-00-00 00:00:00","jobmetaId":"8","job_week_days":"Monday,Thursday","job_time_duration":"45","parentCategoryName":"family","subCategoryName":"Tuition / Extra lessons","distance_in_km":"3210.5","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"115","name":"suraj","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/tuIRBabHGm7lFKEc.jpg","jobId":"143","category_id":"13","job_start_date":"2018-10-17","job_end_date":"2018-11-10","job_offer_rate":"","job_location":"Jyväskylä, Finland","job_latitude":"62.2426034","job_longitude":"25.747256699999998","job_description":"fight\n","crd":"2018-10-17 04:35:07","jobmetaId":"2","job_week_days":"Monday,Wednesday,","job_time_duration":"3","parentCategoryName":"hous & home","subCategoryName":"Cooking / Baking","distance_in_km":"3806.4","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9},{"userId":"124","name":"Siya","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/PBcTnZ3v4miNjtdJ.jpeg","jobId":"149","category_id":"7","job_start_date":"2018-10-22","job_end_date":"2018-10-22","job_offer_rate":"","job_location":"Virginia, USA","job_latitude":"37.4315734","job_longitude":"-78.6568942","job_description":"Gh gfh gah gah fg gh fg gh fg","crd":"0000-00-00 00:00:00","jobmetaId":"3","job_week_days":"Monday,Tuesday","job_time_duration":"44","parentCategoryName":"family","subCategoryName":"Babysitting","distance_in_km":"8171.8","job_confirmed":"3","currentDateTime":"2018-10-22 06:13:35","record_count":9}]
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

    public static class DataBean implements Serializable{
        /**
         * userId : 115
         * name : suraj
         * profileImage : http://dev.livewire.work/./uploads/profile/thumb/tuIRBabHGm7lFKEc.jpg
         * jobId : 142
         * category_id : 8
         * job_start_date : 2018-10-17
         * job_end_date : 2018-12-14
         * job_offer_rate :
         * job_location : India
         * job_latitude : 20.593684000000003
         * job_longitude : 78.96288
         * job_description : hdjhkh hdih
         * crd : 2018-10-17 04:18:35
         * jobmetaId : 1
         * job_week_days : Monday,
         * job_time_duration : 5
         * parentCategoryName : family
         * subCategoryName : Tuition / Extra lessons
         * distance_in_km : 0.0
         * job_confirmed : 3
         * currentDateTime : 2018-10-22 06:13:35
         * record_count : 9
         *
         *
         *
         *   "jobId": "324",
         "category_id": "7",
         "user_id": "210",
         "job_start_date": "2020-12-06",
         "job_budget": "200",
         "job_type": "1",
         "request_status": "4",
         "upd": "2018-12-06 13:23:21",
         "parent_category": "Family",
         "sub_category": "Babysitting",
         "userId": "210",
         "name": "rohitclient",
         "http": "Czvg",
         "https": "Czvgu",
         "profileImage": "https://livewire.work/./uploads/profile/thumb/CzvguIfdYblKG2c7.jpeg",
         "distance_in_km": "6096.9",
         "rating": "2"
         */

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
        private String job_description="";
        private String crd="";
        private String jobmetaId="";
        private String job_week_days="";
        private String job_time_duration="";
        private String parentCategoryName="";
        private String subCategoryName="";
        private String distance_in_km="";
        private String job_confirmed="";
        private String currentDateTime="";
        private String min_rate="";
        private String max_rate="";
        private String job_type="";
        private String rating="";
        private String request_status="";

        private int record_count;

        public String getRequest_status() {
            return request_status;
        }

        public void setRequest_status(String request_status) {
            this.request_status = request_status;
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

        public String getJob_end_date() {
            return job_end_date;
        }

        public void setJob_end_date(String job_end_date) {
            this.job_end_date = job_end_date;
        }

        public String getJob_offer_rate() {
            return job_offer;
        }

        public void setJob_offer_rate(String job_offer_rate) {
            this.job_offer = job_offer_rate;
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

        public String getCurrentDateTime() {
            return currentDateTime;
        }

        public void setCurrentDateTime(String currentDateTime) {
            this.currentDateTime = currentDateTime;
        }

        public int getRecord_count() {
            return record_count;
        }

        public void setRecord_count(int record_count) {
            this.record_count = record_count;
        }

        public String getJob_type() {
            return job_type;
        }

        public void setJob_type(String job_type) {
            this.job_type = job_type;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }
}
