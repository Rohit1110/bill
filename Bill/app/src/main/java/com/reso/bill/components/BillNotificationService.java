package com.reso.bill.components;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.reso.bill.MainActivity;
import com.reso.bill.R;
import com.reso.bill.generic.GenericTransactionsActivity;

/**
 * Created by Admin on 22/03/2019.
 */

public class BillNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("#### RECEIVED " + remoteMessage.getData() + " ######");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = "PayPerBill";
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Intent resultIntent = new Intent(this, GenericTransactionsActivity.class);
        if (notification.getClickAction() == null || !notification.getClickAction().equals("transactionsIntent")) {
            resultIntent = new Intent(this, MainActivity.class);
        }
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.ic_payperbill_logo).setContentTitle(notification.getTitle()).setContentText(notification.getBody()).setAutoCancel(true).setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        /*if (notification.getClickAction() != null && notification.getClickAction().equals("transactionsIntent")) {
            Intent i = new Intent(getApplicationContext(), GenericTransactionsActivity.class);
            startActivity(i);
        }*/
    }


}
