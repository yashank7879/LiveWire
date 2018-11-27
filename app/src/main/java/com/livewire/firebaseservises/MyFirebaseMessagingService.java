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
import com.livewire.ui.activity.JobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.JobOnGoingDetailWorkerActivity;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.ui.activity.notification_activity.NotificationJobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationJobOnGoingDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationMyOnGoingJobDetailClientActivity;
import com.livewire.ui.activity.notification_activity.NotificationMySingleJobDetailClientActivity;
import com.livewire.utils.Constant;


/**
 * Created by mindiii on 6/23/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String CURRENTTIME = "currentTime";
    public static final String USER_ID = "reference_id";
    public static final String CONSTANTTYPE = "type";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String userId = remoteMessage.getData().get(USER_ID);
        String message = remoteMessage.getData().get("body");
        String currentTime = remoteMessage.getData().get(CURRENTTIME);
        String tittle = remoteMessage.getData().get("title");
        String type = remoteMessage.getData().get(CONSTANTTYPE);
        Constant.printLogMethod(Constant.LOG_VALUE, "notifcation", remoteMessage.getData().toString());

       /* Log.e("firebase message body", remoteMessage.getData().get("body"));
        Log.e("firebase message body", remoteMessage.getData().toString());
        Log.e("firebase message body", remoteMessage.getData().get("title"));*/
        sendNotification(userId, message, currentTime, tittle, type);
    }

    private void sendNotification(String userId, String message, String currentTime, String tittle, String type) {
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

        PendingIntent pendingIntent = null;

        switch (type) {
            case "Ongoing_job_request": {
                Intent intent = new Intent(this, NotificationJobOnGoingDetailWorkerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, userId);
                intent.putExtra("body", message);
                intent.putExtra(CONSTANTTYPE, type);
                pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                break;
            }
         /*   case "Once_job_created": {
                Intent intent = new Intent(this, NotificationJobHelpOfferedDetailWorkerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, userId);
                intent.putExtra("body", message);
                intent.putExtra(CONSTANTTYPE, type);
                pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

                break;
            }*/
            case "Once_job_created":
            case "Once_job_rejected":
            case "Once_job_accepted": {
                Intent intent = new Intent(this, NotificationJobHelpOfferedDetailWorkerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, userId);
                intent.putExtra("body", message);
                intent.putExtra(CONSTANTTYPE, type);
                pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

                break;
            }
            case "Once_job_request":
             {
                Intent intent = new Intent(this, NotificationMySingleJobDetailClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, userId);
                intent.putExtra("body", message);
                intent.putExtra(CONSTANTTYPE, type);
                pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

                break;
            }
            case "Ongoing_job_accepted":
            case "Ongoing_job_rejected":
             {
                Intent intent = new Intent(this, NotificationMyOnGoingJobDetailClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, userId);
                intent.putExtra("body", message);
                intent.putExtra(CONSTANTTYPE, type);
                pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

                break;
            }

            default: {
                Intent intent = new Intent(this, WorkerMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, userId);
                intent.putExtra("body", message);
                intent.putExtra(CONSTANTTYPE, type);
                pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                break;
            }
        }

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
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.livelogo))
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

/*
{reference_id=184,
 body=Viss123 sent you request forBabysittingjob,
  type=Ongoing_job_request,
   sound=default,
   title=New Ongoing job request,
    click_action=ChatActivity,
     createrId=}*/
