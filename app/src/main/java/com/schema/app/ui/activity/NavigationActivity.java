package com.schema.app.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.schema.app.ApiClient;
import com.schema.app.BuildConfig;
import com.schema.app.NaverDirectionsApiService;
import com.schema.app.NaverDirectionsResponse;
import com.schema.app.R;
import com.schema.app.data.model.Event;
import com.schema.app.ui.viewmodel.NavigationViewModel;
import com.schema.app.ui.viewmodel.NavigationViewModelFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "NavigationActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private MapView mapView;
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private PolylineOverlay polyline = new PolylineOverlay();

    private NavigationViewModel viewModel;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        int eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        NavigationViewModelFactory factory = new NavigationViewModelFactory(getApplication(), eventId);
        viewModel = new ViewModelProvider(this, factory).get(NavigationViewModel.class);

        viewModel.getEvent().observe(this, event -> {
            if (event != null) {
                currentEvent = event;
                if (naverMap != null) {
                    setupMapContent();
                }
            }
        });

        findViewById(R.id.btn_arrival_complete).setOnClickListener(v -> {
            Intent intent = new Intent(this, ArrivalActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btn_public_transit).setOnClickListener(v -> openNaverMapForPublicTransit());
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        if (currentEvent != null) {
            setupMapContent();
        }
    }

    private void setupMapContent() {
        LatLng destination = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());
        Marker marker = new Marker();
        marker.setPosition(destination);
        marker.setMap(naverMap);

        naverMap.addOnLocationChangeListener(location -> {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            fetchRouteAndDraw(currentLatLng, destination);
            checkArrival(currentLatLng, destination);
        });
    }

    private void fetchRouteAndDraw(LatLng start, LatLng goal) {
        NaverDirectionsApiService service = ApiClient.getDirectionsApiService(BuildConfig.NAVER_MAPS_CLIENT_ID, BuildConfig.NAVER_MAPS_CLIENT_SECRET);
        String startCoord = start.longitude + "," + start.latitude;
        String goalCoord = goal.longitude + "," + goal.latitude;

        service.getDrivingRoute(startCoord, goalCoord).enqueue(new Callback<NaverDirectionsResponse>() {
            @Override
            public void onResponse(Call<NaverDirectionsResponse> call, Response<NaverDirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getRoute() != null) {
                    List<NaverDirectionsResponse.Path> paths = response.body().getRoute().getTrafast();
                    if (paths != null && !paths.isEmpty()) {
                        List<LatLng> pathCoords = new ArrayList<>();
                        for (List<Double> coord : paths.get(0).path) { // getPath() -> path
                            pathCoords.add(new LatLng(coord.get(1), coord.get(0)));
                        }
                        polyline.setCoords(pathCoords);
                        polyline.setColor(Color.MAGENTA);
                        polyline.setMap(naverMap);
                    }
                }
            }

            @Override
            public void onFailure(Call<NaverDirectionsResponse> call, Throwable t) {
                Log.e(TAG, "Failed to get directions", t);
            }
        });
    }

    private void checkArrival(LatLng current, LatLng destination) {
        if (current.distanceTo(destination) < 50) { // 50미터 이내로 들어오면 도착으로 간주
            Intent intent = new Intent(this, ArrivalActivity.class);
            intent.putExtra("event_id", currentEvent.getId());
            startActivity(intent);
            finish();
        }
    }

    private void openNaverMapForPublicTransit() {
        android.location.Location lastLocation = locationSource.getLastLocation();
        if (lastLocation == null || currentEvent == null) {
            Toast.makeText(this, "현재 위치 또는 목적지 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String url = String.format(Locale.US,
                "nmap://route/public?appname=%s&slat=%f&slng=%f&sname=%s&dlat=%f&dlng=%f&dname=%s",
                getPackageName(),
                lastLocation.getLatitude(),
                lastLocation.getLongitude(),
                URLEncoder.encode("현재 위치", "UTF-8"),
                currentEvent.getLatitude(),
                currentEvent.getLongitude(),
                URLEncoder.encode(currentEvent.getTitle(), "UTF-8")
            );

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.nhn.android.nmap");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.nmap")));
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to encode URL for Naver Map", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override
    protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override
    protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override
    protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override
    protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState); }
    @Override
    public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
