package com.schema.app.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.schema.app.databinding.ActivityPreparationBinding;
import com.schema.app.ui.util.CountdownService;
import com.schema.app.ui.util.NotificationHelper;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PreparationActivity extends AppCompatActivity implements CountdownService.CountdownListener {

    private ActivityPreparationBinding binding;
    private int eventId = -1;
    private String eventTitle = "";

    private CountdownService countdownService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreparationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);
        eventTitle = intent.getStringExtra("event_title");
        long prepTimeMillis = intent.getLongExtra("prep_time_millis", 0);
        int travelTimeMinutes = intent.getIntExtra("travel_time_minutes", 0);

        if (eventId == -1) {
            finish();
            return;
        }

        // UI 업데이트
        binding.tvEventTitle.setText(eventTitle);
        binding.tvTravelTime.setText(travelTimeMinutes + "분");
        // TODO: 날씨 정보는 현재 전달되지 않으므로 임시 텍스트 설정
        binding.tvWeather.setText("-");

        // 서비스 시작 및 바인딩
        startAndBindCountdownService(prepTimeMillis);

        // 버튼 설정
        binding.btnSkipPrep.setText("출발");
        binding.btnSkipPrep.setOnClickListener(v -> navigateToNavigation());
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
            NotificationHelper.showReturnToAppNotification(this, PreparationActivity.class, eventId, eventTitle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            countdownService.setCountdownListener(PreparationActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}