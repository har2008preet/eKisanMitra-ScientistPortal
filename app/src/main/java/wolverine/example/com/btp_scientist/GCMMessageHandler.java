package wolverine.example.com.btp_scientist;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Wolverine on 09-08-2015.
 */
public class GCMMessageHandler extends IntentService {
    public static final int MESSAGE_NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    public GCMMessageHandler() {
        super("GCMMessageHandler");
    }
    public static final String TAG = "GCMNotificationIntentService";

    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String msg = intent.getStringExtra("message");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String farmerName = intent.getStringExtra("farmerName");
        String Querry = intent.getStringExtra("Querry");
        String Number = intent.getStringExtra("Number");
        String cropType = intent.getStringExtra("cropType");
        String relatedField = intent.getStringExtra("relatedField");
        String date = intent.getStringExtra("date");
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification(Querry, farmerName, Number, cropType, relatedField, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification(Querry, farmerName, Number, cropType, relatedField, "Deleted messages on server: "
                        + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                //sendNotification("Received: " + extras.toString());
                sendNotification(Querry,farmerName,Number,cropType,relatedField,date);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private void sendNotification(String Querry, String farmerName, String Number, String cropType, String relatedField, String date) {
        Intent resultIntent = new Intent(this, answer_query.class);
        resultIntent.putExtra("Querry", Querry);
        resultIntent.putExtra("farmerName", farmerName);
        resultIntent.putExtra("Number", Number);
        resultIntent.putExtra("cropType", cropType);
        resultIntent.putExtra("relatedField", relatedField);
        resultIntent.putExtra("date", date);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentText("A Question from your field is Asked")
                .setSubText("Answer Fast")
                .setSmallIcon(R.mipmap.main)
                .setAutoCancel(true)
                .setDefaults(defaults);
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mNotifyBuilder.build());
    }
}