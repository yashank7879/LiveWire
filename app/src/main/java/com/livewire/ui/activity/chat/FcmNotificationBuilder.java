package com.livewire.ui.activity.chat;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mindiii on 12/26/18.
 */

public class FcmNotificationBuilder {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    //""""""""""""""""" livewire live """"""""""""""" //

    private static final String AUTH_KEY = "key=" + "AAAAQknvbWY:APA91bFkVuKKBs-Fs89CZvetn60a9J_JP46-PONqj0LYjEu6cvRcQW4BcVvZXCLLIU-EBzQmvNmA3ZMnNwIS8UEMN02pbcO6fK-08_auO4WJ1X7WUZ1BKVAoUyIlvNSj3tZRq6YvGjGm";

    //""""""""""""""""" livewire dev """"""""""""""" //
 //   private static final String AUTH_KEY = "key=" + "AAAA3IjqkEw:APA91bFGWmuP7Gi7ArGcSwcxdr0Pa8LNbNOxchKBDkgaYNDSrQ07OO1uB_sHLrutaPesSC0XWYgRslGFh6Q5BqFvcF3Mpj_n77Sfrz6Ek2C1rL6ayqd_0V0_kg907v450ccejDWUGHTM";


    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "message";
    private static final String KEY_DATA = "data";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_UID = "uid";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    private String mTitle;
    private String mMessage;
    private String mUsername;
    private String mUid;
    private String mFirebaseToken;
    private String mReceiverFirebaseToken;
    private JSONArray mReceiverFirebaseTokenGroup;
    String payLoadEvent;
    private boolean isGroupChat;


    private FcmNotificationBuilder() {

    }

    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }

    public FcmNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FcmNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }

    public FcmNotificationBuilder username(String username) {
        mUsername = username;
        return this;
    }

    public FcmNotificationBuilder uid(String uid) {
        mUid = uid;
        return this;
    }

    public FcmNotificationBuilder firebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseTokenGroup(JSONArray receiverFirebaseToken) {
        mReceiverFirebaseTokenGroup = receiverFirebaseToken;
        return this;
    }

    public FcmNotificationBuilder eventPayLoad(String payLoadEvent) {
        this.payLoadEvent = payLoadEvent;
        return this;
    }

    public FcmNotificationBuilder isGroupChatModule(Boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
        return this;
    }

    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject data = new JSONObject();
        Map<String,Object> params = new HashMap<>();;

            data.put(KEY_TITLE, mUsername);
            data.put(KEY_TEXT, mMessage);
            data.put(KEY_USERNAME, mUsername);
            data.put(KEY_UID, mUid);
            data.put(KEY_FCM_TOKEN, mFirebaseToken);
            data.put("type", "chat");
            data.put("ChatTitle", mTitle);
            data.put("opponentChatId", mUid);
            data.put("sound", "default");

            data.put("priority","high");
            data.put("body",mMessage);
            data.put("icon","new");
            data.put("title",mTitle);
            data.put("click_action","ChatActivity");
            data.put("other_key", true);
            data.put("badge", "1");
            data.put("content_available", true);
            data.put("sound", "default");

            params.put("to", mReceiverFirebaseToken);
            params.put("title", mTitle);
            params.put("sound", "default");

            params.put("data", data);
            params.put("notification", data);


        return new JSONObject(params);


      /*  jsonObjectBody.put(KEY_DATA, jsonObjectData);
        jsonObjectBody.put(KEY_NOTIFICATION, jsonObjectData);
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken);
*/

    }
}
