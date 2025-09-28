package com.schema.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.schema.app.ApiClient;
import com.schema.app.BuildConfig;
import com.schema.app.GovAddressApiService;
import com.schema.app.GovAddressResponse;
import com.schema.app.R;
import com.schema.app.databinding.ActivityMapAddressSelectorBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapAddressSelectorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapAddressSelector";
    private ActivityMapAddressSelectorBinding binding;
    private NaverMap naverMap;

    private LatLng currentCameraPosition;
    private String currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapAddressSelectorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        binding.btnConfirmLocation.setOnClickListener(v -> {
            if (currentCameraPosition == null || currentAddress == null) {
                Toast.makeText(this, "위치를 확인 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("address", currentAddress);
            resultIntent.putExtra("latitude", currentCameraPosition.latitude);
            resultIntent.putExtra("longitude", currentCameraPosition.longitude);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.addOnCameraIdleListener(() -> {
            binding.tvCurrentAddress.setText("주소 확인 중...");
            currentCameraPosition = naverMap.getCameraPosition().target;
            // fetchAddressForLocation(currentCameraPosition); // GOV_ADDRESS_API_KEY가 주석 처리되어 임시 비활성화
            currentAddress = "(API 비활성화) 위도: " + currentCameraPosition.latitude + ", 경도: " + currentCameraPosition.longitude;
            binding.tvCurrentAddress.setText(currentAddress);
        });
    }

    /**
     * 주어진 좌표에 대한 주소를 요청합니다. (리버스 지오코딩)
     * @param latLng 주소를 찾을 좌표
     */
    private void fetchAddressForLocation(LatLng latLng) {
        // GovAddressApiService service = ApiClient.getGovAddressApiService();
        // String apiKey = BuildConfig.GOV_ADDRESS_API_KEY;
        // String x = String.valueOf(latLng.longitude);
        // String y = String.valueOf(latLng.latitude);

        // Call<GovAddressResponse> call = service.searchCoord(apiKey, x, y, "WGS84", "json");

        // call.enqueue(new Callback<GovAddressResponse>() {
        //     @Override
        //     public void onResponse(@NonNull Call<GovAddressResponse> call, @NonNull Response<GovAddressResponse> response) {
        //         if (response.isSuccessful() && response.body() != null && response.body().results.juso != null && !response.body().results.juso.isEmpty()) {
        //             currentAddress = response.body().results.juso.get(0).roadAddr;
        //             binding.tvCurrentAddress.setText(currentAddress);
        //         } else {
        //             String errorMsg = "이 위치의 주소를 찾을 수 없습니다.";
        //             if (response.body() != null && response.body().results.common != null) {
        //                 errorMsg = response.body().results.common.errorMessage;
        //             }
        //             binding.tvCurrentAddress.setText(errorMsg);
        //             currentAddress = null;
        //         }
        //     }

        //     @Override
        //     public void onFailure(@NonNull Call<GovAddressResponse> call, @NonNull Throwable t) {
        //         Log.e(TAG, "Address search failed", t);
        //         binding.tvCurrentAddress.setText("주소 확인 중 오류 발생");
        //         currentAddress = null;
        //     }
        // });
    }
}
