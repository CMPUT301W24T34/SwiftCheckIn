package com.example.swiftcheckin.organizer;

import static androidx.appcompat.resources.Compatibility.Api18Impl.setAutoCancel;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.swiftcheckin.R;
import com.example.swiftcheckin.attendee.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service for handling Firebase Cloud Messaging.
 * Citations: https://firebase.google.com/docs/cloud-messaging/android/client
 * https://developer.android.com/develop/ui/views/notifications (and further in this link)
 * OpenAI | April 3 , 2024 | ChatGPT | Assist me in implementing the Firebase Cloud Messaging service for sending notifications to the app
 * This class extends FirebaseMessagingService to handle FCM (Firebase Cloud Messaging) messages.
 * It receives incoming messages and processes them accordingly.
 */
public class MyFirebaseMessagingServices extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServices";
    private static final String FOREGROUND_CHANNEL_ID = "com.example.swiftcheckin.foreground";

    /**
     *
     * @param remoteMessage the message received from Firebase Cloud Messaging
     * Called when a new FCM message is received.
     *
     * @param remoteMessage The RemoteMessage object containing the received message.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Received message: " + remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);
            sendNotification(title, body);
        }
    }

    /**
     * Sends a notification with the specified title and body.
     *
     * @param title The title of the notification.
     * @param body  The body of the notification.
     */
    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, return without sending the notification
                return;
            }
        }

        notificationManager.notify(0, builder.build());
    }

    /**
     * Called when the service is created. Creates a notification channel.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    /**
     * Creates a notification channel for the app.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}