package com.schema.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.schema.app.R;
import com.schema.app.data.model.Event;
import com.schema.app.databinding.ActivityAdjustAlarmBinding;
import com.schema.app.ui.util.AlarmScheduler;
import com.schema.app.ui.viewmodel.AdjustAlarmViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 최종 알람 시간을 확인하고 미세 조정하는 화면의 액티비티입니다.
 * 사용자는 이 화면에서 예상 이동 시간과 준비 시간을 조절하여 최종 알람 시간을 설정합니다.
 */
public class AdjustAlarmActivity extends AppCompatActivity {

    private ActivityAdjustAlarmBinding binding;
    private AdjustAlarmViewModel viewModel;

    // Intent와 SharedPreferences를 통해 전달받은 데이터
    private int eventId = -1;
    private String eventTitle;
    private long appointmentTimeMillis;
    private String destinationAddress;
    private double destinationLat, destinationLng;
    private double homeLat, homeLng;
    private String selectedTravelMode = "driving";

    // 사용자가 UI를 통해 조절한 시간 값 (분 단위)
    private int adjustedPrepTimeMinutes;
    private int adjustedTravelTimeMinutes;

    /**
     * 액티비티 생성 시 호출됩니다.
     * 뷰와 ViewModel을 초기화하고, Intent 및 SharedPreferences에서 데이터를 로드합니다.
     * ViewModel을 통해 API 호출을 시작하고, UI 리스너와 옵저버를 설정합니다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdjustAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AdjustAlarmViewModel.class);

        loadInitialData();
        setupObservers();
        setupAdjustmentListeners();

        // ViewModel을 통해 이동 시간과 날씨 정보를 비동기적으로 요청합니다.
        viewModel.calculateTravelTime(homeLat, homeLng, destinationLat, destinationLng);
        viewModel.fetchWeatherInfo(destinationLat, destinationLng, appointmentTimeMillis);

        binding.btnConfirm.setOnClickListener(v -> saveFinalEvent());
    }

    /**
     * 이전 액티비티(AddEventActivity)와 SharedPreferences에서 초기 데이터를 로드합니다.
     */
    private void loadInitialData() {
        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);
        eventTitle = intent.getStringExtra("event_title");
        appointmentTimeMillis = intent.getLongExtra("appointment_time", 0);
        destinationAddress = intent.getStringExtra("destination_address");
        destinationLat = intent.getDoubleExtra("destination_lat", 0);
        destinationLng = intent.getDoubleExtra("destination_lng", 0);

        SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        homeLat = sharedPreferences.getFloat(SettingsActivity.KEY_HOME_LATITUDE, 0);
        homeLng = sharedPreferences.getFloat(SettingsActivity.KEY_HOME_LONGITUDE, 0);
        adjustedPrepTimeMinutes = sharedPreferences.getInt(SettingsActivity.KEY_PREP_TIME, 60);
    }

    /**
     * ViewModel의 LiveData 변경을 감지하는 옵저버들을 설정합니다.
     * 이동 시간, 날씨 정보 등의 변경을 감지하여 UI를 업데이트합니다.
     */
    private void setupObservers() {
        viewModel.getTravelTimeMinutes().observe(this, minutes -> {
            adjustedTravelTimeMinutes = minutes;
            updateUiWithCalculatedTimes();
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            // TODO: ProgressBar 등을 추가하여 로딩 상태를 시각적으로 표시할 수 있습니다.
        });

        viewModel.getWeatherInfo().observe(this, weather -> {
            binding.tvWeather.setText(weather);
        });
    }

    /**
     * 최종적으로 계산된 정보를 바탕으로 이벤트를 저장(추가 또는 수정)합니다.
     */
    private void saveFinalEvent() {
        long prepTimeMillis = TimeUnit.MINUTES.toMillis(adjustedPrepTimeMinutes);
        long travelTimeMillis = TimeUnit.MINUTES.toMillis(adjustedTravelTimeMinutes);
        long startPreparationTimeMillis = appointmentTimeMillis - travelTimeMillis - prepTimeMillis;

        if (eventId != -1) {
            // 수정 모드: 기존 이벤트를 가져와 내용을 업데이트합니다.
            viewModel.getEventById(eventId).observe(this, event -> {
                if (event != null) {
                    event.setTitle(eventTitle);
                    event.setEventTimeMillis(appointmentTimeMillis);
                    event.setAddress(destinationAddress);
                    event.setLatitude(destinationLat);
                    event.setLongitude(destinationLng);
                    event.setPreparationTimeMillis(prepTimeMillis);
                    event.setTravelTimeMillis(travelTimeMillis);
                    event.setTravelMode(selectedTravelMode);
                    event.setStartPreparationTimeMillis(startPreparationTimeMillis);

                    viewModel.update(event);
                    AlarmScheduler.scheduleAlarm(this, event); // 수정된 정보로 알람을 다시 스케줄합니다.
                    Toast.makeText(this, "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(this, MainActivity.class));
                }
            });
        } else {
            // 생성 모드: 새로운 Event 객체를 생성하고 저장합니다.
            Event newEvent = new Event(eventTitle, appointmentTimeMillis, prepTimeMillis, travelTimeMillis, selectedTravelMode,
                    destinationAddress, destinationLat, destinationLng, 0, 0, startPreparationTimeMillis, 0, 0, false);
            viewModel.insert(newEvent, newId -> {
                newEvent.setId(newId.intValue());
                AlarmScheduler.scheduleAlarm(this, newEvent); // 생성된 ID로 알람을 스케줄합니다.
            });
            Toast.makeText(this, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            finishAffinity();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * 시간 조절을 위한 SeekBar, 버튼, 라디오 그룹에 대한 리스너를 설정합니다.
     */
    private void setupAdjustmentListeners() {
        binding.seekbarTravelTime.setMax(180);
        binding.seekbarPrepTime.setMax(180);

        binding.btnTravelDown.setOnClickListener(v -> updateTime(-5, binding.seekbarTravelTime, true));
        binding.btnTravelUp.setOnClickListener(v -> updateTime(5, binding.seekbarTravelTime, true));
        binding.seekbarTravelTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    adjustedTravelTimeMinutes = progress;
                    updateUiWithCalculatedTimes();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.btnPrepDown.setOnClickListener(v -> updateTime(-5, binding.seekbarPrepTime, false));
        binding.btnPrepUp.setOnClickListener(v -> updateTime(5, binding.seekbarPrepTime, false));
        binding.seekbarPrepTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    adjustedPrepTimeMinutes = progress;
                    updateUiWithCalculatedTimes();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.radioGroupTransport.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_driving) {
                selectedTravelMode = "driving";
                viewModel.calculateTravelTime(homeLat, homeLng, destinationLat, destinationLng);
            } else if (checkedId == R.id.radio_public_transit) {
                selectedTravelMode = "transit";
                Toast.makeText(this, "대중교통 길찾기는 아직 지원되지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 버튼 클릭이나 SeekBar 조작으로 시간을 조절하고 UI를 업데이트합니다.
     * @param delta 변경할 시간 (분)
     * @param seekBar 조작된 SeekBar
     * @param isTravelTime 이동 시간 조절 여부
     */
    private void updateTime(int delta, SeekBar seekBar, boolean isTravelTime) {
        int currentValue = seekBar.getProgress();
        int newValue = Math.max(0, currentValue + delta);
        seekBar.setProgress(newValue);
        if (isTravelTime) {
            adjustedTravelTimeMinutes = newValue;
        } else {
            adjustedPrepTimeMinutes = newValue;
        }
        updateUiWithCalculatedTimes();
    }

    /**
     * 조절된 시간을 바탕으로 최종 알람 시간 등을 계산하고 화면의 텍스트를 업데이트합니다.
     */
    private void updateUiWithCalculatedTimes() {
        long prepTimeMillis = TimeUnit.MINUTES.toMillis(adjustedPrepTimeMinutes);
        long travelTimeMillis = TimeUnit.MINUTES.toMillis(adjustedTravelTimeMinutes);
        long startPreparationTimeMillis = appointmentTimeMillis - travelTimeMillis - prepTimeMillis;

        SimpleDateFormat sdf = new SimpleDateFormat("M월 d일, a h:mm", Locale.getDefault());
        String alarmTimeString = sdf.format(new Date(startPreparationTimeMillis));

        binding.tvCalculatedAlarmTime.setText(alarmTimeString + "에 알람이 설정됩니다.");
        binding.tvTravelTime.setText("예상 이동 시간: " + adjustedTravelTimeMinutes + "분");
        binding.tvPrepTime.setText("예상 준비 시간: " + adjustedPrepTimeMinutes + "분");

        // SeekBar 진행 상태도 업데이트
        binding.seekbarTravelTime.setProgress(adjustedTravelTimeMinutes);
        binding.seekbarPrepTime.setProgress(adjustedPrepTimeMinutes);
    }
}