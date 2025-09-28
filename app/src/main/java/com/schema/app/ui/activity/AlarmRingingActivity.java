package com.schema.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.schema.app.databinding.ActivityAlarmRingingBinding;
import com.schema.app.ui.util.NotificationHelper;

public class AlarmRingingActivity extends AppCompatActivity {

    private ActivityAlarmRingingBinding binding;
    private Vibrator vibrator;
    private int eventId = -1;
    private String eventTitle = "";
    private long prepTimeMillis = 0;
    private int travelTimeMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmRingingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);
        eventTitle = intent.getStringExtra("event_title");
        prepTimeMillis = intent.getLongExtra("prep_time_millis", 0);
        travelTimeMinutes = intent.getIntExtra("travel_time_minutes", 0);

        if (eventId == -1) {
            finish();
            return;
        }

        binding.tvEventTitlePlaceholder.setText("\"" + eventTitle + "\"");

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        } else {
            vibrator.vibrate(pattern, 0);
        }

        binding.btnStopVibration.setOnClickListener(v -> stopVibrationAndNavigate());
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
            NotificationHelper.showReturnToAppNotification(this, AlarmRingingActivity.class, eventId, eventTitle);
        }
    }

    private void stopVibrationAndNavigate() {
        if (vibrator != null) {
            vibrator.cancel();
        }
        Intent intent = new Intent(this, PreparationActivity.class);
        intent.putExtra("event_id", eventId);
        intent.putExtra("event_title", eventTitle);
        intent.putExtra("prep_time_millis", prepTimeMillis);
        intent.putExtra("travel_time_minutes", travelTimeMinutes);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) {
            vibrator.cancel();
        }
        NotificationHelper.cancelReturnToAppNotification(this);
    }
}