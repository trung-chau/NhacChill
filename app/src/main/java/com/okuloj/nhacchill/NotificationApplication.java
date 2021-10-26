package com.okuloj.nhacchill;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class NotificationApplication extends Application {
    public static final String CHANNEL_ID = "com.okuloj.foregroundservice.CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();
    }

    private void createChannelNotification() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                "Channel ForegroundService",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setSound(null, null);
        NotificationManager manager = this.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }
}
