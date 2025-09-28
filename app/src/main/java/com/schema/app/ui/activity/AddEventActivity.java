package com.schema.app.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.schema.app.GovAddressResponse;
import com.schema.app.R;
import com.schema.app.data.model.Event;
import com.schema.app.databinding.ActivityAddEventBinding;
import com.schema.app.ui.viewmodel.AddEventViewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityAddEventBinding binding;
    private Calendar selectedDateTime = Calendar.getInstance();
    private NaverMap naverMap;
    private Marker marker = new Marker();
    private AddEventViewModel addEventViewModel;
    private ArrayAdapter<String> addressAdapter;
    private List<GovAddressResponse.Juso> jusoList = new ArrayList<>();

    private Event eventToEdit = null;
    private LatLng selectedCoords;
    private String selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addEventViewModel = new ViewModelProvider(this).get(AddEventViewModel.class);
        binding.setViewmodel(addEventViewModel);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        setupAutoCompleteTextView();
        setupClickListeners();
        setupObservers();
        updateDateTimeButtonText();

        int eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1) {
            loadEventForEdit(eventId);
        } else {
            long selectedEpochDay = getIntent().getLongExtra("selected_date_epoch_day", -1);
            if (selectedEpochDay != -1) {
                LocalDate passedDate = LocalDate.ofEpochDay(selectedEpochDay);
                selectedDateTime.set(Calendar.YEAR, passedDate.getYear());
                selectedDateTime.set(Calendar.MONTH, passedDate.getMonthValue() - 1);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, passedDate.getDayOfMonth());
                updateDateTimeButtonText();
            }
        }
    }

    private void loadEventForEdit(int eventId) {
        setTitle("일정 수정");
        addEventViewModel.getEventById(eventId).observe(this, event -> {
            if (event != null) {
                eventToEdit = event;
                prefillUiForEdit(event);
            }
        });
    }

    private void prefillUiForEdit(Event event) {
        binding.editTextTitle.setText(event.getTitle());

        selectedDateTime.setTimeInMillis(event.getEventTimeMillis());
        updateDateTimeButtonText();

        if (event.getEventTimeMillis() < System.currentTimeMillis()) {
            binding.buttonSetDate.setEnabled(false);
            binding.buttonSetTime.setEnabled(false);
            Toast.makeText(this, "지난 일정은 날짜를 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        selectedAddress = event.getAddress();
        binding.editTextAddress.setText(selectedAddress, false);

        selectedCoords = new LatLng(event.getLatitude(), event.getLongitude());
        updateMapLocation();
    }

    private void setupAutoCompleteTextView() {
        addressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        binding.editTextAddress.setAdapter(addressAdapter);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        if (eventToEdit != null) {
            updateMapLocation();
        }
    }

    private void setupObservers() {
        addEventViewModel.getAddressSuggestions().observe(this, suggestions -> {
            addressAdapter.clear();
            addressAdapter.addAll(suggestions);
            addressAdapter.notifyDataSetChanged();
        });

        addEventViewModel.getJusoList().observe(this, jusoList -> {
            this.jusoList = jusoList;
        });

        addEventViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBarAddress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void setupClickListeners() {
        binding.buttonSetDate.setOnClickListener(v -> showDatePicker());
        binding.buttonSetTime.setOnClickListener(v -> showTimePicker());

        binding.editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.editTextAddress.isPerformingCompletion()) { return; }
                if (s.length() > 1) {
                    addEventViewModel.searchAddress(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.editTextAddress.setOnItemClickListener((parent, view, position, id) -> {
            if (jusoList != null && position < jusoList.size()) {
                GovAddressResponse.Juso selectedJuso = jusoList.get(position);
                selectedAddress = selectedJuso.roadAddr;
                binding.editTextAddress.setText(selectedAddress, false);

                try {
                    double lat = Double.parseDouble(selectedJuso.entY);
                    double lng = Double.parseDouble(selectedJuso.entX);
                    selectedCoords = new LatLng(lat, lng);
                    updateMapLocation();
                } catch (NumberFormatException e) {
                    Toast.makeText(AddEventActivity.this, "주소의 좌표 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.buttonSave.setOnClickListener(v -> {
            String title = binding.editTextTitle.getText().toString();
            if (title.trim().isEmpty()) {
                Toast.makeText(this, "일정 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedCoords == null) {
                Toast.makeText(this, "먼저 장소를 검색하고 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            navigateToAdjustAlarmActivity(title, selectedAddress, selectedCoords.latitude, selectedCoords.longitude);
        });

        binding.buttonSkipLocation.setOnClickListener(v -> {
            String title = binding.editTextTitle.getText().toString();
            if (title.trim().isEmpty()) {
                Toast.makeText(this, "일정 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            navigateToAdjustAlarmActivity(title, "임시 주소", 37.5665, 126.9780);
        });
    }

    private void navigateToAdjustAlarmActivity(String title, String address, double lat, double lng) {
        Intent intent = new Intent(this, AdjustAlarmActivity.class);
        intent.putExtra("event_title", title);
        intent.putExtra("appointment_time", selectedDateTime.getTimeInMillis());
        intent.putExtra("destination_address", address);
        intent.putExtra("destination_lat", lat);
        intent.putExtra("destination_lng", lng);
        if (eventToEdit != null) {
            intent.putExtra("event_id", eventToEdit.getId());
        }
        startActivity(intent);
        finish();
    }

    private void updateMapLocation() {
        if (naverMap != null && selectedCoords != null) {
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(selectedCoords);
            naverMap.moveCamera(cameraUpdate);
            marker.setPosition(selectedCoords);
            marker.setMap(naverMap);
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateTimeButtonText();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);
            updateDateTimeButtonText();
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void updateDateTimeButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        binding.buttonSetDate.setText(dateFormat.format(selectedDateTime.getTime()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH시 mm분", Locale.getDefault());
        binding.buttonSetTime.setText(timeFormat.format(selectedDateTime.getTime()));
    }
}