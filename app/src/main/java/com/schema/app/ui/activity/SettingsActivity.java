package com.schema.app.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.schema.app.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "ScheMaPrefs";
    public static final String KEY_HOME_ADDRESS = "home_address";
    public static final String KEY_HOME_LATITUDE = "home_latitude";
    public static final String KEY_HOME_LONGITUDE = "home_longitude";
    public static final String KEY_PREP_TIME = "prep_time_minutes";
    public static final String KEY_PRE_ALARM_TIME = "pre_alarm_time_minutes"; // New Key

    private ActivitySettingsBinding binding;
    private SharedPreferences sharedPreferences;

    private String selectedAddress;
    private double selectedLat;
    private double selectedLng;

    private final ActivityResultLauncher<Intent> mapAddressLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    selectedAddress = data.getStringExtra("address");
                    selectedLat = data.getDoubleExtra("latitude", 0);
                    selectedLng = data.getDoubleExtra("longitude", 0);
                    binding.tvHomeAddress.setText(selectedAddress);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loadSettings();

        binding.btnChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapAddressSelectorActivity.class);
            mapAddressLauncher.launch(intent);
        });

        binding.buttonSaveSettings.setOnClickListener(v -> validateAndSave());
    }

    private void loadSettings() {
        selectedAddress = sharedPreferences.getString(KEY_HOME_ADDRESS, "");
        int prepTime = sharedPreferences.getInt(KEY_PREP_TIME, 60);
        int preAlarmTime = sharedPreferences.getInt(KEY_PRE_ALARM_TIME, 5); // Load pre-alarm time
        selectedLat = sharedPreferences.getFloat(KEY_HOME_LATITUDE, 0);
        selectedLng = sharedPreferences.getFloat(KEY_HOME_LONGITUDE, 0);

        if (selectedAddress.isEmpty()) {
            binding.tvHomeAddress.setText("주소를 설정해주세요.");
        } else {
            binding.tvHomeAddress.setText(selectedAddress);
        }
        binding.editTextPrepTime.setText(String.valueOf(prepTime));
        binding.editTextPreAlarmTime.setText(String.valueOf(preAlarmTime)); // Set pre-alarm time
    }

    private void validateAndSave() {
        String prepTimeString = binding.editTextPrepTime.getText().toString();
        String preAlarmTimeString = binding.editTextPreAlarmTime.getText().toString();

        if (selectedAddress == null || selectedAddress.isEmpty() || prepTimeString.trim().isEmpty()) {
            Toast.makeText(this, "주소와 준비 시간을 모두 설정해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int prepTime = Integer.parseInt(prepTimeString);
            // pre-alarm time is optional, defaults to 5 if empty
            int preAlarmTime = TextUtils.isEmpty(preAlarmTimeString) ? 5 : Integer.parseInt(preAlarmTimeString);
            
            saveSettingsToPrefs(selectedAddress, prepTime, preAlarmTime, selectedLat, selectedLng);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "시간은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSettingsToPrefs(String address, int prepTime, int preAlarmTime, double lat, double lon) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_HOME_ADDRESS, address);
        editor.putInt(KEY_PREP_TIME, prepTime);
        editor.putInt(KEY_PRE_ALARM_TIME, preAlarmTime); // Save pre-alarm time
        editor.putFloat(KEY_HOME_LATITUDE, (float) lat);
        editor.putFloat(KEY_HOME_LONGITUDE, (float) lon);
        editor.apply();

        Toast.makeText(this, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}