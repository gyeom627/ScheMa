package com.schema.app.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.schema.app.ApiClient;
import com.schema.app.BuildConfig;
import com.schema.app.GovAddressApiService;
import com.schema.app.GovAddressResponse;
import com.schema.app.databinding.ActivityInitialSetupBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitialSetupActivity extends AppCompatActivity {

    private static final String TAG = "InitialSetupActivity";
    private ActivityInitialSetupBinding binding;
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
        binding = ActivityInitialSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);

        binding.btnSetAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapAddressSelectorActivity.class);
            mapAddressLauncher.launch(intent);
        });

        binding.buttonStart.setOnClickListener(v -> validateAndSave());

        // '건너뛰기' 버튼 클릭 시 (임시)
        binding.buttonSkipSetup.setOnClickListener(v -> {
            // 더미 값으로 저장하고 메인으로 이동
            saveSettingsToPrefs("임시 집 주소", 60, 37.5665, 126.9780);
        });
    }

    private void validateAndSave() {
        String prepTimeString = binding.editTextPrepTime.getText().toString();

        if (selectedAddress == null || prepTimeString.trim().isEmpty()) {
            Toast.makeText(this, "주소와 준비 시간을 모두 설정해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int prepTime = Integer.parseInt(prepTimeString);
            // geocodeHomeAddress(selectedAddress, prepTime); // GOV_ADDRESS_API_KEY가 주석 처리되어 임시 비활성화
            saveSettingsToPrefs(selectedAddress, prepTime, selectedLat, selectedLng);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "준비 시간은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // private void geocodeHomeAddress(String homeAddress, int prepTime) {
    //     GovAddressApiService service = ApiClient.getGovAddressApiService();
    //     String apiKey = BuildConfig.GOV_ADDRESS_API_KEY;
    //     Call<GovAddressResponse> call = service.searchAddress(apiKey, 1, 10, homeAddress, "json");

    //     call.enqueue(new Callback<GovAddressResponse>() {
    //         @Override
    //         public void onResponse(@NonNull Call<GovAddressResponse> call, @NonNull Response<GovAddressResponse> response) {
    //             if (response.isSuccessful() && response.body() != null && response.body().results.juso != null && !response.body().results.juso.isEmpty()) {
    //                 GovAddressResponse.Juso firstJuso = response.body().results.juso.get(0);
    //                 double latitude = Double.parseDouble(firstJuso.entY);
    //                 double longitude = Double.parseDouble(firstJuso.entX);
    //                 saveSettingsToPrefs(homeAddress, prepTime, latitude, longitude);
    //             } else {
    //                 Toast.makeText(InitialSetupActivity.this, "유효하지 않은 주소입니다. 주소를 확인해주세요.", Toast.LENGTH_LONG).show();
    //             }
    //         }

    //         @Override
    //         public void onFailure(@NonNull Call<GovAddressResponse> call, @NonNull Throwable t) {
    //                 Log.e(TAG, "Home address geocoding failed", t);
    //                 Toast.makeText(InitialSetupActivity.this, "주소 변환 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
    //         }
    //     });
    // }

    private void saveSettingsToPrefs(String homeAddress, int prepTime, double latitude, double longitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SettingsActivity.KEY_HOME_ADDRESS, homeAddress);
        editor.putInt(SettingsActivity.KEY_PREP_TIME, prepTime);
        editor.putFloat(SettingsActivity.KEY_HOME_LATITUDE, (float) latitude);
        editor.putFloat(SettingsActivity.KEY_HOME_LONGITUDE, (float) longitude);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
