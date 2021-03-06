package com.livewire.firebaseservises;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.livewire.R;
import com.livewire.ui.activity.ClientMainActivity;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.ui.activity.notification_activity.NotificationCompleteJobOnGoingDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationJobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationJobOnGoingDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationMyOnGoingJobDetailClientActivity;
import com.livewire.ui.activity.notification_activity.NotificationMySingleJobDetailClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;

import static com.livewire.utils.Constant.MY_UID;

/**
 * Created by mindiii on 6/23/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String CURRENTTIME = "currentTime";
    public static final String USER_ID = "reference_id";
    public static final String CONSTANTTYPE = "type";
    private String opponentChatId = "";
    private String titleName = "";
    private String userType = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String userId = remoteMessage.getData().get(USER_ID);
        String message = remoteMessage.getData().get("body");
        String currentTime = remoteMessage.getData().get(CURRENTTIME);
        String tittle = remoteMessage.getData().get("title");
        userType = remoteMessage.getData().get("for_user_type");
        String type = remoteMessage.getData().get(CONSTANTTYPE);
        // Log.e( "noti usermode: ",userType);
        if (remoteMessage.getData().get("opponentChatId") != null) {
            opponentChatId = remoteMessage.getData().get("opponentChatId");
            titleName = remoteMessage.getData().get("titleName");
        }
        Constant.printLogMethod(Constant.LOG_VALUE, "notifcation", remoteMessage.getData().toString());
        sendNotification(userId, message, currentTime, tittle, type, opponentChatId, titleName);
    }

    private void sendNotification(String userId, String message, String currentTime, String tittle, String type, String opponentChatId, String titleName) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = null;
        switch (type) {
            case "Ongoing_job_request": {
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
                    Intent intent = new Intent(this, NotificationJobOnGoingDetailWorkerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    intent.putExtra("for_user_type",userType);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else {
                    Intent intent = new Intent(this, ClientMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MyProfile", "MyProfile");
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                }

                break;
            }

            case "Once_job_created": {
//                Toast.makeText(this,PreferenceConnector.readString(this,PreferenceConnector.USER_MODE,""),Toast.LENGTH_SHORT).show();
                // Log.e("User mode: ", PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, ""));
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
                    Intent intent = new Intent(this, NotificationJobHelpOfferedDetailWorkerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    intent.putExtra("for_user_type",userType);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equalsIgnoreCase("client")) {
                    Intent intent = new Intent(this, ClientMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MyProfile", "MyProfile");
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                }
                break;
            }
            case "Once_job_completed": {
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
                    Intent intent = new Intent(this, NotificationJobHelpOfferedDetailWorkerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    intent.putExtra("for_user_type",userType);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else {
                    Intent intent = new Intent(this, ClientMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MyProfile", "MyProfile");
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                }
                break;
            }
            case "Ongoing_job_completed": {
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
                    Intent intent = new Intent(this, NotificationCompleteJobOnGoingDetailWorkerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    intent.putExtra("for_user_type",userType);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else {
                    Intent intent = new Intent(this, ClientMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MyProfile", "MyProfile");
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                }
                break;
            }

            case "Once_job_rejected":
            case "Once_job_accepted": {
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
                    Intent intent = new Intent(this, NotificationJobHelpOfferedDetailWorkerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    intent.putExtra("for_user_type",userType);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else {
                    Intent intent = new Intent(this, ClientMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MyProfile", "MyProfile");
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                }
                break;
            }

            case "Once_job_request": {
                Log.e("User mode: ", PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, ""));
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
                    Intent intent = new Intent(this, NotificationMySingleJobDetailClientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    intent.putExtra("for_user_type",userType);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else {
                    Intent intent = new Intent(this, WorkerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MyProfile", "MyProfile");
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                    // showAlertWorkerDialog();
                }
                break;
            }
            case "Ongoing_job_accepted":
            case "Ongoing_job_rejected": {
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
                    Intent intent = new Intent(this, NotificationMyOnGoingJobDetailClientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    intent.putExtra("for_user_type",userType);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else {
                    Intent intent = new Intent(this, WorkerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("MyProfile", "MyProfile");
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                }
                break;
            }

            case "chat": {
                if (!MY_UID.equals(opponentChatId)) {
                    if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equalsIgnoreCase("worker")) {
                        Intent intent = new Intent(this, WorkerMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.putExtra(USER_ID, userId);
                        intent.putExtra("opponentChatId", opponentChatId);
                        intent.putExtra("titleName", titleName);
                        intent.putExtra(CONSTANTTYPE, type);
                        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(tittle, message, pendingIntent);
                    } else if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equalsIgnoreCase("client")){
                        Intent intent = new Intent(this, ClientMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // intent.putExtra(USER_ID, userId);
                        intent.putExtra("opponentChatId", opponentChatId);
                        intent.putExtra("titleName", titleName);
                        intent.putExtra(CONSTANTTYPE, type);
                        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(tittle, message, pendingIntent);
                    }
                }
                break;
            }
            default: {
                if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equalsIgnoreCase("worker")) {
                    Intent intent = new Intent(this, WorkerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                } else {
                    Intent intent = new Intent(this, ClientMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(USER_ID, userId);
                    intent.putExtra("body", message);
                    intent.putExtra(CONSTANTTYPE, type);
                    pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(tittle, message, pendingIntent);
                }
                break;
            }
        }
    }

    private void sendNotification(String tittle, String message, PendingIntent pendingIntent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = "my_channel_01";// The id of the channel.
        CharSequence name = "Abc";// The user-visible name of the channel.
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationChannel mChannel = null;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notify_icon_blk)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(tittle)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}

/*{reference_id=21,
 body=Yash three has completed your job Babysitting,
 type=Once_job_completed, sound=default, title=Job ended,
  click_action=NotificationJobHelpOfferedDetailWorkerActivity,
for_user_type=worker}*/
/*

{reference_id=22, body=Yash three has completed your job Tuition / Extra lessons,
 type=Ongoing_job_completed, sound=default, title=Job ended,
click_action=NotificationJobHelpOfferedDetailWorkerActivity, for_user_type=worker}

{{job_type=1, reference_id=69,
 body=Yash rathore job accepted,
  type=Once_job_accepted, sound=default,
   title=Job request accepted,
click_action=NotificationJobHelpOfferedDetailWorkerActivity, for_user_type=worker}

{reference_id=3, body=Palak created a Babysitting job, type=Once_job_created,
 sound=default, title=New help offered,
 click_action=NotificationJobHelpOfferedDetailWorkerActivity,
  for_user_type=worker}


{reference_id=2,
        body=Yashank sent you request forTuition / Extra lessons job, type=Ongoing_job_request,
        sound=default,
        title=New Ongoing Work request, click_action=NotificationJobHelpOfferedDetailWorkerActivity}

{reference_id=31,
 body=Yash three sent you request for Babysitting job, type=Once_job_request, sound=default, title=New Once job request,
click_action=NotificationJobHelpOfferedDetailWorkerActivity, for_user_type=client}


{job_type=2, reference_id=32, body=Yash three job accepted,
type=Ongoing_job_accepted, sound=default,
title=Job request accepted, click_action=NotificationJobHelpOfferedDetailWorkerActivity,
for_user_type=client}
*/
