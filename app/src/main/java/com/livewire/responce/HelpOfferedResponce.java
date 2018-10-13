package com.livewire.responce;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 10/2/18.
 */

public class HelpOfferedResponce implements Serializable {
    /**
     * status : success
     * message : OK
     * data : [{"userId":"2","name":"shekhar","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/dBhYSx8ZuHcmV0C5.jpg","jobId":"1","category_id":"8","job_start_date":"2018-10-25","job_budget":"1000","job_location":"indore",
     * "job_latitude":"1.02136","job_longitude":"1.3256","job_description":"test demo",
     * "crd":"2018-09-23 23:04:47","parentCategoryName":"family","subCategoryName":"Tuition / Extra lessons"
     * ,"distance_in_km":"186.1"},{"userId":"2","name":"shekhar","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/dBhYSx8ZuHcmV0C5.jpg","jobId":"8","category_id":"9","job_start_date":"2018-10-25","job_budget":"12","job_location":"Malviya Nagar, Indore, Madhya Pradesh, India","job_latitude":"22.7426158","job_longitude":"75.89660030000005","job_description":"test demo","crd":"2018-10-01 03:25:03","parentCategoryName":"family","subCategoryName":"Transport","distance_in_km":"8563.5"},{"userId":"2","name":"shekhar","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/dBhYSx8ZuHcmV0C5.jpg","jobId":"9","category_id":"9","job_start_date":"2018-10-25","job_budget":"12","job_location":"Malviya Nagar, Indore, Madhya Pradesh, India","job_latitude":"22.7426158","job_longitude":"75.89660030000005","job_description":"test demo","crd":"2018-10-01 03:26:41","parentCategoryName":"family","subCategoryName":"Transport","distance_in_km":"8563.5"},{"userId":"25","name":"gsjs","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/5fRr3OjLBGgxAUwK.jpg","jobId":"10","category_id":"7","job_start_date":"2018-10-03","job_budget":"2","job_location":"Sri Lanka","job_latitude":"7.873053999999999","job_longitude":"80.77179699999999","job_description":"gddhjd","crd":"2018-10-01 03:34:21","parentCategoryName":"family","subCategoryName":"Babysitting","distance_in_km":"8991.2"},{"userId":"26","name":"Bhaskar","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/qlnKDI5JuUFBzy3t.jpeg","jobId":"3","category_id":"7","job_start_date":"2018-09-26","job_budget":"22","job_location":"J K Puri, Kanpur, Uttar Pradesh, India","job_latitude":"26.428924","job_longitude":"80.3964824","job_description":"Ugh fog gf. H","crd":"2018-09-25 22:50:12","parentCategoryName":"family","subCategoryName":"Babysitting","distance_in_km":"9052.2"}]
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
         *
         * userId : 2
         * name : shekhar
         * profileImage : http://dev.livewire.work/./uploads/profile/thumb/dBhYSx8ZuHcmV0C5.jpg
         * jobId : 1
         * category_id : 8
         * job_start_date : 2018-10-25
         * job_budget : 1000
         * job_location : indore
         * job_latitude : 1.02136
         * job_longitude : 1.3256
         * job_description : test demo
         * crd : 2018-09-23 23:04:47
         * parentCategoryName : family
         * subCategoryName : Tuition / Extra lessons
         * distance_in_km : 186.1
         */

        private String userId;
        private String name;
        private String profileImage;
        private String jobId;
        private String category_id;
        private String job_start_date;
        private String job_budget;
        private String job_location;
        private String job_latitude;
        private String job_longitude;
        private String job_description;
        private String crd;
        private String parentCategoryName;
        private String subCategoryName;
        private String distance_in_km;
        private String currentTime;
        private String job_confirmed;

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

        public String getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }

        public String getJob_confirmed() {
            return job_confirmed;
        }

        public void setJob_confirmed(String job_confirmed) {
            this.job_confirmed = job_confirmed;
        }
    }
}
