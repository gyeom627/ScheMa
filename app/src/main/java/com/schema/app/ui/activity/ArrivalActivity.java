package com.schema.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.schema.app.databinding.ActivityArrivalBinding;
import com.schema.app.ui.viewmodel.ArrivalViewModel;

/**
 * 도착 완료를 알리는 화면입니다.
 */
public class ArrivalActivity extends AppCompatActivity {

    private ActivityArrivalBinding binding;
    private ArrivalViewModel viewModel;
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArrivalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ArrivalViewModel.class);

        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1) {
            markEventAsCompleted();
        }

        binding.btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void markEventAsCompleted() {
        viewModel.getEventById(eventId).observe(this, event -> {
            if (event != null && !event.isCompleted()) {
                event.setCompleted(true);
                viewModel.update(event);
                Toast.makeText(this, "일정이 완료 처리되었습니다.", Toast.LENGTH_SHORT).show();
                // 한 번만 업데이트하기 위해 observe를 중단합니다.
                viewModel.getEventById(eventId).removeObservers(this);
            }
        });
    }
}