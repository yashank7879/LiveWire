package com.livewire.multiple_file_upload;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.livewire.ui.activity.CompleteProfileActivity;
import com.livewire.utils.PreferenceConnector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;

import static com.livewire.utils.ApiCollection.BASE_URL;

/**
 * Created by Chiranjib Ganguly on 12-Feb-18.
 */

public class MultiPartRequest extends Request<String> {

    private Response.Listener<String> mListener;
    private HttpEntity mHttpEntity;
    private Map<String, String> mParams;
    private CompleteProfileActivity activity;


    public MultiPartRequest(Response.ErrorListener errorListener, Response.Listener listener, List<File> file, int numberOfFiles, Map<String, String> params, CompleteProfileActivity activity) {
        super(Method.POST, BASE_URL+"user/updateWorkerProfile", errorListener);
        mListener = listener;
        mParams=params;
        mHttpEntity = buildMultipartEntity(file, numberOfFiles);
        this.activity=activity;
    }
/*
    public MultiPartRequest(Response.ErrorListener errorListener, Response.Listener listener, ArrayList<File> tmpFile, int size, ArrayList<File> profileImageFileList, int size1, HashMap<String, String> mPram, CompleteProfileActivity activity) {
        super(Method.POST, "http://dev.livewire.work/service/user/updateWorkerProfile", errorListener);
        mListener = listener;
        mParams=mPram;
        mHttpEntity = buildMultipartEntity(tmpFile, size,profileImageFileList,size1);
        this.activity=activity;
    }*/

    private HttpEntity buildMultipartEntity(List<File> file, int numberOffiles) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for(int i=0; i < file.size();i++){
            FileBody fileBody = new FileBody(file.get(i));
            builder.addPart(Template.Query.INTRO_VIDEO_KEY, fileBody);
        }



        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addTextBody(key, value);
        }

        return builder.build();
    }

   /* public MultiPartRequest(Response.ErrorListener errorListener, Response.Listener listener, ArrayList<File> tmpFile, int size, ArrayList<File> profileImageFileList, int size1, HashMap<String, String> mPram, CompleteProfileActivity activity) {
        super(Method.POST, "http://dev.mindiii.com/mabwe/service/user/addPost", errorListener);
        mListener = listener;
        mParams=mPram;
        mHttpEntity = buildMultipartEntity(tmpFile, size,profileImageFileList,size1);
        this.activity=activity;
    }

    private HttpEntity buildMultipartEntity(List<File> file, int numberOffiles,List<File> profileImageFile,int size) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for(int i=0; i < file.size();i++){
            FileBody fileBody = new FileBody(file.get(i));
            builder.addPart("video", fileBody);
        }

        for(int i=0; i < profileImageFile.size();i++){
            FileBody fileBody = new FileBody(profileImageFile.get(i));
            builder.addPart("video_thumb", fileBody);
        }

        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addTextBody(key, value);
        }

        return builder.build();
    }*/
 public MultiPartRequest(Response.ErrorListener errorListener, Response.Listener listener, ArrayList<File> tmpFile, int size, ArrayList<File> profileImageFileList, int size1, HashMap<String, String> mPram, CompleteProfileActivity activity) {
     super(Method.POST, BASE_URL+"user/updateWorkerProfile", errorListener);
     mListener = listener;
        mParams=mPram;
        mHttpEntity = buildMultipartEntity(tmpFile, size,profileImageFileList,size1);
        this.activity=activity;
    }

    private HttpEntity buildMultipartEntity(List<File> file, int numberOffiles,List<File> profileImageFile,int size) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for(int i=0; i < file.size();i++){
            FileBody fileBody = new FileBody(file.get(i));
            builder.addPart(Template.Query.INTRO_VIDEO_KEY, fileBody);
        }

        for(int i=0; i < profileImageFile.size();i++){
            FileBody fileBody = new FileBody(profileImageFile.get(i));
            builder.addPart(Template.Query.IMAGE_KEY, fileBody);
        }

        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addTextBody(key, value);
        }

        return builder.build();
    }



    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> header = new HashMap<>();
        header.put("authToken", PreferenceConnector.readString(activity,PreferenceConnector.AUTH_TOKEN,""));
        return header;
    }

 /*   @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> header = new HashMap<>();
        header.put("authToken", PreferenceConnector.readString(activity, PreferenceConnector.AUTH_TOKEN, ""));
        return header;
    }*/

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            mHttpEntity.writeTo(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            VolleyLog.e("" + e);
            return bos.toByteArray();
        } catch (OutOfMemoryError e){
            VolleyLog.e("" + e);
            return bos.toByteArray();
        }

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, "UTF-8"),
                    getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            Log.d("MultipartRequest",e.getMessage());
            return Response.success(new String(response.data),
                    getCacheEntry());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

}