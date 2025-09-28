package com.schema.app.ui.util;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;

import com.schema.app.R;
import com.schema.app.ui.activity.PrepareActivity;

import java.util.Locale;

public class CountdownService extends Service {

    private static final String TAG = "CountdownService";
    private static final String CHANNEL_ID = "countdown_channel";
    private static final int NOTIFICATION_ID = 102;

    private final IBinder binder = new LocalBinder();
    private Handler handler;
    private Runnable runnable;

    private long totalPreparationTimeMillis;
    private long preparationStartTime;
    private boolean isCountdownActive = false;

    public interface CountdownListener {
        void onCountdownTick(long millisUntilFinished);
        void onCountdownFinished();
    }

    private CountdownListener listener;

    public void setCountdownListener(CountdownListener listener) {
        this.listener = listener;
    }

    public class LocalBinder extends Binder {
        public CountdownService getService() {
            return CountdownService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            totalPreparationTimeMillis = intent.getLongExtra("EXTRA_PREP_TIME_MILLIS", 0);
            preparationStartTime = System.currentTimeMillis();

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(500);
                }
            }

            startForeground(NOTIFICATION_ID, buildNotification("준비 중...", ""));
            if (totalPreparationTimeMillis > 0) {
                isCountdownActive = true;
                startCountdown();
            }
        }
        return START_STICKY;
    }

    private void startCountdown() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isCountdownActive) return;
                long elapsedTime = System.currentTimeMillis() - preparationStartTime;
                long currentRemainingTime = totalPreparationTimeMillis - elapsedTime;

                if (currentRemainingTime > 0) {
                    if (listener != null) {
                        listener.onCountdownTick(currentRemainingTime);
                    }
                    updateNotification(currentRemainingTime);
                    handler.postDelayed(this, 1000);
                } else {
                    if (listener != null) {
                        listener.onCountdownFinished();
                    }
                    stopSelf();
                }
            }
        };
        handler.post(runnable);
    }

    private Notification buildNotification(String title, String content) {
        Intent notificationIntent = new Intent(this, PrepareActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0L})
                .setOnlyAlertOnce(true)
                .build();
    }

    private void updateNotification(long millisUntilFinished) {
        if (handler == null) return;
        long minutes = (millisUntilFinished / 1000) / 60;
        long seconds = (millisUntilFinished / 1000) % 60;
        String content = String.format(Locale.getDefault(), "출발까지 %02d분 %02d초 남음", minutes, seconds);
        Notification notification = buildNotification("일정 준비 중", content);
        getSystemService(NotificationManager.class).notify(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "카운트다운 알림 채널",
                    NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.enableVibration(false);
            serviceChannel.setVibrationPattern(new long[]{0L});
            getSystemService(NotificationManager.class).createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        isCountdownActive = false;
        stopForeground(true);
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
            runnable = null;
        }
        getSystemService(NotificationManager.class).cancel(NOTIFICATION_ID);
    }

    public long getCurrentRemainingTime() {
        if (!isCountdownActive) {
            return totalPreparationTimeMillis;
        }
        return totalPreparationTimeMillis - (System.currentTimeMillis() - preparationStartTime);
    }
}