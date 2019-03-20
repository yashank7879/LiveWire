package com.livewire.utils;

/**
 * Created by mindiii on 9/27/18.
 */

public class ApiCollection {
    ///""""""""""' dev api  """""""""//
    //public static final String BASE_URL = "http://dev.livewire.work/apiv2/";

    ///""""""""""' live api  """""""""//
    /*  public static final String BASE_URL = "https://livewire.work/service/";*/
    public static final String BASE_URL = "https://livewire.work/apiv2/";


    public static final String UPDATE_CLIENT_PROFILE_API = "user/updateClientProfile";
    public static final String FORGOT_PASSWORD_API = "user/forgotPassword";
    public static final String USER_REGISTRATION_API = "user/userRegistration";
    public static final String USER_LOGIN_API = "user/userLogin";
    public static final String GET_CATEGORY_LIST_API = "user/getCategoryList";
    public static final String CLIENT_JOB_POST_API = "Jobpost/clientJobPost";
    public static final String GET_MY_JOB_LIST_API = "Jobpost/getMyJobList";
    public static final String UPDATE_WORKER_PROFILEAPI = "user/updateWorkerProfile";
    public static final String CHANGE_USER_MODE_API = "user/changeUserMode";
    public static final String NEAR_BY_USER_API = "user/nearByUser";

    //http://dev.livewire.work/apiv2/user/changeUserMode
    //http://dev.livewire.work/apiv2/Jobpost/clientJobPost

    public static final String JOBPOSTSEND_REQUEST_2_API = "Jobpost/sendRequest2";
    public static final String JOBPOSTSEND_GET_JOB_DETAIL_API = "Jobpost/getJobDetail";
    public static final String JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API = "Jobpost/get_worker_job_detail";
    public static final String JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API = "Jobpost/get_client_job_detail";
    public static final String GET_NEAR_BY_WORKER_API = "Jobpost/getNearByWorker";
    public static final String COMPLETE_JOB_LIST_API = "Jobpost/completeJobList";
    public static final String USER_LOGOUT_API = "user/logout";
    public static final String GET_JOB_LIST_API = "Jobpost/getJobList";
    public static final String CHECK_SOCIAL_STATUS_API = "user/checkSocialStatus";


    public static final String ADD_BANK_ACCOUNT_API = "payment/addBankAccount";
    public static final String GET_BANK_DETAILS_API = "payment/getBankDetails";
    public static final String GET_MY_PROFILE_API = "user/getMyProfile";
    public static final String GET_SUBCATEGORY_LIST_API = "user/getSubcategoryList";
    public static final String GET_REVIEW_LIST_API = "user/getReviewList";

    public static final String ADD_REVIEW_API = "user/addReview";
    public static final String GET_MY_USER_PROFILE_API = "user/getUserProfile";
    public static final String GET_MY_JOB_REQUEST_LIST_API = "Jobpost/getMyJobRequestList";
    public static final String CONFIRM_OR_COMPLETED_JOB_LIST_API = "Jobpost/confirmOrCompletedJobList?";
    public static final String END_JOB_API = "payment/endJob"; // single job payment
    public static final String ONGOING_END_JOB_API = "payment/ongoingEndJob"; // ongoing job payment
    public static final String NOTIFICATION_LIST_API = "user/notificationList"; //https://livewire.work/service/user/notificationList
    public static final String CANCLE_JOB_BY_CLIENT_API = "Jobpost/cancleJobByClient"; //https://livewire.work/service/Jobpost/cancleJobByClient
    public static final String CONFIRMATION_DATE_OF_BIRTH_API = "user/confirmationDateOfBirth"; //https://livewire.work/service/Jobpost/cancleJobByClient

}
