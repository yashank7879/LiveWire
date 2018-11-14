package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 10/16/18.
 */

public class RequestResponceClient {
    /**
     * status : success
     * message : OK
     * data : [{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"2","name":"shekhar","distance_in_km":"0.0","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/dBhYSx8ZuHcmV0C5.jpg"},{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"56","name":"Yashu","distance_in_km":"4983.8","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg"},{"userId":"27","name":"purva","distance_in_km":"5389.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"27","name":"purva","distance_in_km":"5389.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"27","name":"purva","distance_in_km":"5389.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"91","name":"Hello","distance_in_km":"5270.4","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/thumb/ZqhajPm24AifIYEF.jpeg"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"3","name":"Priya Verma","distance_in_km":"7286.2","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"},{"userId":"107","name":"CTom Cruise","distance_in_km":"5200.7","request_status":"0","profileImage":"http://dev.livewire.work/./uploads/profile/default.png"}]
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
         * userId : 56
         * name : Yashu
         * distance_in_km : 4983.8
         * request_status : 0
         * profileImage : http://dev.livewire.work/./uploads/profile/thumb/vcyf0PR2Gr3LVgCb.jpeg
         */

        private String userId="";
        private String name="";
        private String distance_in_km="";
        private String request_status="";
        private String profileImage="";
        private String job_confirmed="";

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

        public String getDistance_in_km() {
            return distance_in_km;
        }

        public void setDistance_in_km(String distance_in_km) {
            this.distance_in_km = distance_in_km;
        }

        public String getRequest_status() {
            return request_status;
        }

        public void setRequest_status(String request_status) {
            this.request_status = request_status;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getJob_confirmed() {
            return job_confirmed;
        }

        public void setJob_confirmed(String job_confirmed) {
            this.job_confirmed = job_confirmed;
        }
    }
}
