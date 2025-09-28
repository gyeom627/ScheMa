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
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.schema.app.R;
import com.schema.app.ui.util.CountdownService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AlarmActivity extends AppCompatActivity implements CountdownService.CountdownListener {

    private int eventId = -1;
    private Vibrator vibrator;
    private CountdownService countdownService;
    private boolean isBound = false;

    private TextView tvCountdown;
    private TextView tvTravelTime;
    private TextView tvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvCountdown = findViewById(R.id.tv_countdown);
        tvTravelTime = findViewById(R.id.tv_travel_time);
        tvWeather = findViewById(R.id.tv_weather);
        Button btnDepartNow = findViewById(R.id.btn_depart_now);
        Button btnStopVibration = findViewById(R.id.btn_stop_vibration);

        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);
        long prepTimeMillis = intent.getLongExtra("prep_time_millis", 0);
        long travelTimeMillis = intent.getLongExtra("travel_time_millis", 0);

        if (eventId == -1) {
            finish();
            return;
        }

        // UI Update
        long travelMinutes = TimeUnit.MILLISECONDS.toMinutes(travelTimeMillis);
        tvTravelTime.setText("예상 이동 시간: " + travelMinutes + "분");
        tvWeather.setText("날씨: (API 비활성화)"); // 날씨 정보는 추후 연동

        startVibration();
        startAndBindCountdownService(prepTimeMillis);

        btnStopVibration.setOnClickListener(v -> {
            stopVibration();
            v.setVisibility(View.GONE);
        });
        btnDepartNow.setOnClickListener(v -> navigateToNavigation());
    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 1000};
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            } else {
                vibrator.vibrate(pattern, 0);
            }
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
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        if (prepTimeMillis <= 0) {
            onCountdownFinished();
        }
    }

    private void navigateToNavigation() {
        stopVibration();
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
    protected void onDestroy() {
        super.onDestroy();
        stopVibration();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    public void onCountdownTick(long millisUntilFinished) {
        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        // hh:mm 형식을 위해 시간과 분만 사용
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
        tvCountdown.setText(timeFormatted);
    }

    @Override
    public void onCountdownFinished() {
        tvCountdown.setText("00:00");
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            CountdownService.LocalBinder binder = (CountdownService.LocalBinder) service;
            countdownService = binder.getService();
            countdownService.setCountdownListener(AlarmActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}