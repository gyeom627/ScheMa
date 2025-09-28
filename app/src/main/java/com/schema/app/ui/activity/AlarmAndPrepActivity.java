package com.schema.app.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.schema.app.databinding.ActivityAlarmAndPrepBinding;
import com.schema.app.ui.util.CountdownService;
import com.schema.app.ui.util.NotificationHelper;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AlarmAndPrepActivity extends AppCompatActivity implements CountdownService.CountdownListener {

    private ActivityAlarmAndPrepBinding binding;
    private int eventId = -1;
    private String eventTitle = "";

    private Vibrator vibrator;
    private CountdownService countdownService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmAndPrepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);
        eventTitle = intent.getStringExtra("event_title");
        long prepTimeMillis = intent.getLongExtra("prep_time_millis", 0);

        if (eventId == -1) {
            finish();
            return;
        }

        binding.tvEventTitle.setText(eventTitle);

        startVibration();
        startAndBindCountdownService(prepTimeMillis);

        binding.btnStopVibration.setOnClickListener(v -> {
            stopVibration();
            v.setVisibility(View.GONE); // Hide the button after use
        });

        binding.btnDepartNow.setOnClickListener(v -> navigateToNavigation());
    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        } else {
            vibrator.vibrate(pattern, 0);
        }
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void startAndBindCountdownService(long prepTimeMillis) {
        Intent serviceIntent = new Intent(this, CountdownService.class);
        serviceIntent.putExtra("EXTRA_PREP_TIME_MILLIS", prepTimeMillis);
        startService(serviceIntent);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private void navigateToNavigation() {
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        stopService(new Intent(this, CountdownService.class));

        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("event_id", eventId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationHelper.cancelReturnToAppNotification(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing()) {
            NotificationHelper.showReturnToAppNotification(this, AlarmAndPrepActivity.class, eventId, eventTitle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVibration();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        NotificationHelper.cancelReturnToAppNotification(this);
    }

    // --- CountdownService Listener & ServiceConnection ---
    @Override
    public void onCountdownTick(long millisUntilFinished) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.tvPrepCountdown.setText(timeFormatted);
    }

    @Override
    public void onCountdownFinished() {
        binding.tvPrepCountdown.setText("00:00");
        navigateToNavigation();
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            CountdownService.LocalBinder binder = (CountdownService.LocalBinder) service;
            countdownService = binder.getService();
            countdownService.setCountdownListener(AlarmAndPrepActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}
