package com.livewire.responce;

import java.util.List;

/**
 * Created by mindiii on 12/17/18.
 */

public class NotificationResponce {
    /**
     * status : success
     * message : OK
     * data : [{"notificationId":"50","notification_by":"221","notification_message":{"title":"Job request accepted","body":" job accepted","type":"Once_job_accepted","reference_id":"378","click_action":"ChatActivity"},"notification_type":"Once_job_accepted","read_status":"0","crd":"2018-12-17 07:26:40","upd":"0000-00-00 00:00:00","name":"yashank rathore","http":"","https":"","profileImage":"https://livewire.work/./uploads/profile/default.png"},{"notificationId":"49","notification_by":"221","notification_message":{"title":"New Once job request","body":" sent you request for Babysitting job","type":"Once_job_request","reference_id":"378","click_action":"ChatActivity"},"notification_type":"Once_job_request","read_status":"0","crd":"2018-12-17 07:25:57","upd":"0000-00-00 00:00:00","name":"yashank rathore","http":"","https":"","profileImage":"https://livewire.work/./uploads/profile/default.png"},{"notificationId":"48","notification_by":"221","notification_message":{"title":"Job request declined","body":"has declined your Babysitting job request","type":"Once_job_rejected","reference_id":"378","click_action":"ChatActivity"},"notification_type":"Once_job_rejected","read_status":"0","crd":"2018-12-17 07:12:58","upd":"0000-00-00 00:00:00","name":"yashank rathore","http":"","https":"","profileImage":"https://livewire.work/./uploads/profile/default.png"},{"notificationId":"46","notification_by":"221","notification_message":{"title":"New Once job request","body":"sent you request for Babysitting job","type":"Once_job_request","reference_id":"378","click_action":"ChatActivity"},"notification_type":"Once_job_request","read_status":"0","crd":"2018-12-17 07:06:41","upd":"0000-00-00 00:00:00","name":"yashank rathore","http":"","https":"","profileImage":"https://livewire.work/./uploads/profile/default.png"}]
     * currentDateTime : 2018-12-17 08:52:48
     */

    private String status;
    private String message;
    private String currentDateTime;
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

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * notificationId : 50
         * notification_by : 221
         * notification_message : {"title":"Job request accepted","body":" job accepted","type":"Once_job_accepted","reference_id":"378","click_action":"ChatActivity"}
         * notification_type : Once_job_accepted
         * read_status : 0
         * crd : 2018-12-17 07:26:40
         * upd : 0000-00-00 00:00:00
         * name : yashank rathore
         * http :
         * https :
         * profileImage : https://livewire.work/./uploads/profile/default.png
         */

        private String notificationId;
        private String notification_by;
        private NotificationMessageBean notification_message;
        private String notification_type;
        private String read_status;
        private String crd;
        private String upd;
        private String name;
        private String http;
        private String https;
        private String profileImage;

        public String getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(String notificationId) {
            this.notificationId = notificationId;
        }

        public String getNotification_by() {
            return notification_by;
        }

        public void setNotification_by(String notification_by) {
            this.notification_by = notification_by;
        }

        public NotificationMessageBean getNotification_message() {
            return notification_message;
        }

        public void setNotification_message(NotificationMessageBean notification_message) {
            this.notification_message = notification_message;
        }

        public String getNotification_type() {
            return notification_type;
        }

        public void setNotification_type(String notification_type) {
            this.notification_type = notification_type;
        }

        public String getRead_status() {
            return read_status;
        }

        public void setRead_status(String read_status) {
            this.read_status = read_status;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHttp() {
            return http;
        }

        public void setHttp(String http) {
            this.http = http;
        }

        public String getHttps() {
            return https;
        }

        public void setHttps(String https) {
            this.https = https;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public static class NotificationMessageBean {
            /**
             * title : Job request accepted
             * body :  job accepted
             * type : Once_job_accepted
             * reference_id : 378
             * click_action : ChatActivity
             */

            private String title;
            private String body;
            private String type;
            private String reference_id;
            private String click_action;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getBody() {
                return body;
            }

            public void setBody(String body) {
                this.body = body;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getReference_id() {
                return reference_id;
            }

            public void setReference_id(String reference_id) {
                this.reference_id = reference_id;
            }

            public String getClick_action() {
                return click_action;
            }

            public void setClick_action(String click_action) {
                this.click_action = click_action;
            }
        }
    }
}
