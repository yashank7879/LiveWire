package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 10/8/18.
 */

public class JobDetailWorkerResponce {

    /**
     * status : success
     * message : OK
     * data : [{"userId":"2","jobId":"9","category_id":"9","job_start_date":"2018-10-25","job_budget":"12","job_latitude":"22.7426158","job_longitude":"75.89660030000005","job_description":"test demo","crd":"2018-10-01 03:26:41","parent_category":"family","sub_category":"Transport","distance_in_km":"5203.3","total_request":"1","job_confirmed":"0","currentTime":"2018-10-08 02:31:49","requestedUserData":[{"userId":"56","name":"Yashu","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg","request_status":"0","distance_in_km":"325.9"}]}]
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
         * userId : 2
         * jobId : 9
         * category_id : 9
         * job_start_date : 2018-10-25
         * job_budget : 12
         * job_latitude : 22.7426158
         * job_longitude : 75.89660030000005
         * job_description : test demo
         * crd : 2018-10-01 03:26:41
         * parent_category : family
         * sub_category : Transport
         * distance_in_km : 5203.3
         * total_request : 1
         * job_confirmed : 0
         * currentTime : 2018-10-08 02:31:49
         * requestedUserData : [{"userId":"56","name":"Yashu","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg","request_status":"0","distance_in_km":"325.9"}]
         */

        private String userId;
        private String jobId;
        private String category_id;
        private String job_start_date;
        private String job_budget;
        private String job_latitude;
        private String job_longitude;
        private String job_description;
        private String crd;
        private String parent_category;
        private String sub_category;
        private String distance_in_km;
        private String total_request;
        private String job_confirmed;
        private String currentTime;
        private List<RequestedUserDataBean> requestedUserData;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getDistance_in_km() {
            return distance_in_km;
        }

        public void setDistance_in_km(String distance_in_km) {
            this.distance_in_km = distance_in_km;
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

        public String getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }

        public List<RequestedUserDataBean> getRequestedUserData() {
            return requestedUserData;
        }

        public void setRequestedUserData(List<RequestedUserDataBean> requestedUserData) {
            this.requestedUserData = requestedUserData;
        }

        public static class RequestedUserDataBean {
            /**
             * userId : 56
             * name : Yashu
             * profileImage : http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg
             * request_status : 0
             * distance_in_km : 325.9
             */

            private String userId;
            private String name;
            private String profileImage;
            private String request_status;
            private String distance_in_km;

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
        }
    }
}
