package com.schema.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.schema.app.R;
import com.schema.app.data.model.Event;
import com.schema.app.databinding.ActivityEventDetailBinding;
import com.schema.app.ui.viewmodel.EventDetailViewModel;
import com.schema.app.ui.viewmodel.EventDetailViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 일정의 상세 정보를 보여주는 액티비티입니다.
 * 사용자는 이 화면에서 일정의 모든 정보를 확인하고, 수정 또는 삭제할 수 있습니다.
 */
public class EventDetailActivity extends AppCompatActivity {

    private ActivityEventDetailBinding binding;
    private EventDetailViewModel viewModel;
    private Event currentEvent; // 현재 화면에 표시된 이벤트 객체

    /**
     * 액티비티 생성 시 호출됩니다.
     * Intent로부터 event_id를 받아 ViewModel을 설정하고, 이벤트 정보를 불러와 UI에 표시합니다.
     * 수정 및 삭제 버튼에 대한 리스너를 설정합니다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MainActivity로부터 전달받은 이벤트 ID를 가져옵니다.
        int eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId == -1) {
            Toast.makeText(this, "오류: 일정을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        binding.setLifecycleOwner(this);

        // ViewModel에 eventId를 전달하기 위해 팩토리를 사용합니다.
        EventDetailViewModelFactory factory = new EventDetailViewModelFactory(getApplication(), eventId);
        viewModel = new ViewModelProvider(this, factory).get(EventDetailViewModel.class);

        binding.setViewModel(viewModel);

        // ViewModel의 이벤트 LiveData를 관찰하여 UI를 업데이트합니다.
        viewModel.getEvent().observe(this, event -> {
            if (event != null) {
                currentEvent = event;
                updateUI(event);
            }
        });

        // 삭제 버튼 클릭 시, ViewModel을 통해 이벤트를 삭제하고 화면을 종료합니다.
        binding.btnDelete.setOnClickListener(v -> {
            if (currentEvent != null) {
                viewModel.delete(currentEvent);
                Toast.makeText(this, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // 수정 버튼 클릭 시, AddEventActivity를 수정 모드로 시작합니다.
        binding.btnEdit.setOnClickListener(v -> {
            if (currentEvent != null) {
                Intent intent = new Intent(this, AddEventActivity.class);
                intent.putExtra("event_id", currentEvent.getId());
                startActivity(intent);
                finish(); // 상세 화면은 닫습니다.
            }
        });
    }

    /**
     * 전달받은 Event 객체의 정보로 화면의 텍스트 뷰들을 업데이트합니다.
     * @param event UI에 표시할 Event 객체
     */
    private void updateUI(Event event) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        binding.detailTitle.setText(event.getTitle());
        binding.detailEventTime.setText("약속 시간: " + sdf.format(new Date(event.getEventTimeMillis())));
        binding.detailAddress.setText("장소: " + event.getAddress());

        long prepMinutes = TimeUnit.MILLISECONDS.toMinutes(event.getPreparationTimeMillis());
        binding.detailPreparationTime.setText("준비 시간: " + prepMinutes + "분");

        long travelMinutes = TimeUnit.MILLISECONDS.toMinutes(event.getTravelTimeMillis());
        binding.detailTravelTime.setText("예상 이동 시간: " + travelMinutes + "분");

        binding.detailFinalAlarmTime.setText("최종 알람 시간: " + sdf.format(new Date(event.getStartPreparationTimeMillis())));
    }
}