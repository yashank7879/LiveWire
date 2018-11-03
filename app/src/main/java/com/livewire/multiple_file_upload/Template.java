package com.livewire.multiple_file_upload;

/**
 * Created by Farooq Khan on 14-Jul-16.
 */
public interface Template {

    interface VolleyRetryPolicy{
        int SOCKET_TIMEOUT = 1000 * 100;
        int RETRIES = 0;
    }

    interface Query{
        String INTRO_VIDEO_KEY = "intro_video";
        String IMAGE_KEY = "profileImage";

        String VIDEO_THUMB_IMAGE = "video_thumb";
        String VALUE_DIRECTORY = "Uploads";
        String KEY_CODE = "kode";
        String KEY_MESSAGE = "pesan";
        String VALUE_CODE_SUCCESS = "2";
        String VALUE_CODE_FAILED = "1";
        String VALUE_CODE_MISSING = "0";
    }

    interface Code{
        int CAMERA_IMAGE_CODE = 0;
        int CAMERA_VIDEO_CODE = 1;
        int FILE_MANAGER_CODE = 2;
        int AUDIO_CODE = 3;
    }


}
