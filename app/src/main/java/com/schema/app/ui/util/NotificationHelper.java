package com.schema.app.ui.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.schema.app.R;

public class NotificationHelper {

    private static final String RETURN_CHANNEL_ID = "ScheMaReturnChannel";
    public static final int RETURN_NOTIFICATION_ID = 111; // Use a unique ID

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ScheMa 앱 복귀 알림";
            String description = "진행 중인 알람/준비 화면으로 돌아갑니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(RETURN_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showReturnToAppNotification(Context context, Class<?> activityClass, int eventId, String eventTitle) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, activityClass);
        intent.putExtra("event_id", eventId);
        intent.putExtra("event_title", eventTitle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, RETURN_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // Use a system default icon
                .setContentTitle("\"" + eventTitle + "\" 일정이 진행 중입니다.")
                .setContentText("탭하여 화면으로 돌아가기")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Makes the notification persistent
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(RETURN_NOTIFICATION_ID, notification);
    }

    public static void cancelReturnToAppNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(RETURN_NOTIFICATION_ID);
    }
}