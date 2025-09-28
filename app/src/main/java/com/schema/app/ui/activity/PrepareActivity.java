package com.schema.app.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.naver.maps.map.util.FusedLocationSource;
import com.schema.app.BuildConfig;
import com.schema.app.R;
import com.schema.app.data.model.AppDatabase;
import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;
import com.schema.app.ui.util.CountdownService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PrepareActivity extends AppCompatActivity implements CountdownService.CountdownListener {

    private static final String TAG = "PrepareActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private int eventId = -1;
    private Event currentEvent;

    private TextView tvPreparationTime, tvWeatherInfo, tvEventTitle, tvTravelTime;
    private ImageView ivWeatherIcon;
    private TableLayout tlWeatherForecast;
    private Button btnPrepareComplete, btnPublicTransit;

    private Vibrator vibrator;
    private CountdownService countdownService;
    private boolean isBound = false;
    private FusedLocationSource locationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Init Views
        tvEventTitle = findViewById(R.id.tv_event_title);
        tvPreparationTime = findViewById(R.id.tv_preparation_time);
        tvTravelTime = findViewById(R.id.tv_travel_time);
        tvWeatherInfo = findViewById(R.id.tv_weather_info);
        ivWeatherIcon = findViewById(R.id.iv_weather_icon);
        tlWeatherForecast = findViewById(R.id.tl_weather_forecast);
        btnPrepareComplete = findViewById(R.id.btn_prepare_complete);
        btnPublicTransit = findViewById(R.id.btn_public_transit_prep);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id", -1);
        String eventTitle = intent.getStringExtra("event_title");
        long prepTimeMillis = intent.getLongExtra("prep_time_millis", 0);
        long travelTimeMillis = intent.getLongExtra("travel_time_millis", 0);
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        if (eventId == -1) {
            finish();
            return;
        }

        // UI 초기값 설정
        tvEventTitle.setText(eventTitle);
        tvTravelTime.setText("예상 이동 시간: " + TimeUnit.MILLISECONDS.toMinutes(travelTimeMillis) + "분");

        startVibration();
        startAndBindCountdownService(prepTimeMillis);
        fetchWeatherFromApi(latitude, longitude);

        btnPrepareComplete.setOnClickListener(v -> navigateToNavigation());
        btnPublicTransit.setOnClickListener(v -> openNaverMapForPublicTransit());
    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 1000};
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void startAndBindCountdownService(long prepTimeMillis) {
        if (prepTimeMillis <= 0) {
            onCountdownFinished();
            return;
        }
        Intent serviceIntent = new Intent(this, CountdownService.class);
        serviceIntent.putExtra("EXTRA_PREP_TIME_MILLIS", prepTimeMillis);
        startService(serviceIntent);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);
    }

    private void navigateToNavigation() {
        stopVibration();
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

    private void openNaverMapForPublicTransit() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        android.location.Location lastLocation = locationSource.getLastLocation();
        if (lastLocation == null) {
            Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
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
    
    private void fetchWeatherFromApi(double lat, double lon) {
        String weatherApiKey = BuildConfig.WEATHER_API_KEY;
        if (weatherApiKey == null || weatherApiKey.isEmpty()) {
            tvWeatherInfo.setText("날씨 API 키가 설정되지 않았습니다.");
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + weatherApiKey + "&units=metric" + "&lang=kr";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                final int responseCode = conn.getResponseCode(); // Move this outside runOnUiThread
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    Gson gson = new Gson();
                    ForecastResponse forecastResponse = gson.fromJson(response.toString(), ForecastResponse.class);
                    runOnUiThread(() -> updateWeatherUI(forecastResponse));
                } else {
                    Log.e(TAG, "Error fetching weather: " + responseCode + ", " + conn.getResponseMessage());
                    runOnUiThread(() -> tvWeatherInfo.setText("날씨 정보 로드 실패: " + responseCode));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching weather", e);
                runOnUiThread(() -> tvWeatherInfo.setText("날씨 정보 로드 오류"));
            }
        });
    }

    private void updateWeatherUI(ForecastResponse forecastResponse) {
        if (forecastResponse != null && forecastResponse.list != null && !forecastResponse.list.isEmpty()) {
            ForecastItem currentForecast = forecastResponse.list.get(0);
            String description = currentForecast.weather.get(0).description;
            double temp = currentForecast.main.temp;
            String iconCode = currentForecast.weather.get(0).icon;

            tvWeatherInfo.setText(String.format(Locale.getDefault(), "현재 날씨: %s, %.1f°C", description, temp));
            ivWeatherIcon.setImageResource(getWeatherIconResId(iconCode));

            tlWeatherForecast.removeAllViews();
            TableRow headerRow = new TableRow(this);
            headerRow.setPadding(8, 8, 8, 8);
            addTextViewToTableRow(headerRow, "시간", true);
            addTextViewToTableRow(headerRow, "날씨", true);
            addTextViewToTableRow(headerRow, "온도", true);
            tlWeatherForecast.addView(headerRow);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            for (int i = 0; i < forecastResponse.list.size(); i++) {
                ForecastItem item = forecastResponse.list.get(i);
                if (i < 8) { // 다음 24시간 (3시간 간격 8개)
                    TableRow row = new TableRow(this);
                    row.setPadding(8, 8, 8, 8);

                    String time = timeFormat.format(new Date(item.dt * 1000L));
                    addTextViewToTableRow(row, time, false);

                    ImageView forecastIcon = new ImageView(this);
                    forecastIcon.setImageResource(getWeatherIconResId(item.weather.get(0).icon));
                    row.addView(forecastIcon);

                    addTextViewToTableRow(row, item.weather.get(0).main, false);
                    addTextViewToTableRow(row, String.format(Locale.getDefault(), "%.1f°C", item.main.temp), false);

                    tlWeatherForecast.addView(row);
                }
            }
        } else {
            tvWeatherInfo.setText("날씨 정보를 가져올 수 없습니다.");
            ivWeatherIcon.setImageResource(0);
            tlWeatherForecast.removeAllViews();
        }
    }

    private int getWeatherIconResId(String iconCode) {
        switch (iconCode) {
            case "01d": case "01n": return R.drawable.ic_weather_clear;
            case "02d": case "02n": case "03d": case "03n": case "04d": case "04n": return R.drawable.ic_weather_clouds;
            case "09d": case "09n": case "10d": case "10n": return R.drawable.ic_weather_rain;
            case "11d": case "11n": return R.drawable.ic_weather_thunderstorm;
            case "13d": case "13n": return R.drawable.ic_weather_snow;
            case "50d": case "50n": return R.drawable.ic_weather_mist;
            default: return R.drawable.ic_weather_default;
        }
    }

    private void addTextViewToTableRow(TableRow row, String text, boolean isHeader) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView.setGravity(Gravity.CENTER);
        if (isHeader) {
            textView.setTextSize(16f);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        row.addView(textView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openNaverMapForPublicTransit();
            } else {
                Toast.makeText(this, "대중교통 길찾기를 위해 위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVibration();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    public void onCountdownTick(long millisUntilFinished) {
        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        tvPreparationTime.setText("출발까지 남은 시간\n" + timeFormatted);
    }

    @Override
    public void onCountdownFinished() {
        tvPreparationTime.setText("출발까지 남은 시간\n00:00:00");
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            CountdownService.LocalBinder binder = (CountdownService.LocalBinder) service;
            countdownService = binder.getService();
            countdownService.setCountdownListener(PrepareActivity.this);
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    // --- Inner classes for Weather Response Parsing ---
    private static class ForecastResponse { @SerializedName("list") List<ForecastItem> list; }
    private static class ForecastItem { @SerializedName("dt") long dt; @SerializedName("main") Main main; @SerializedName("weather") List<Weather> weather; @SerializedName("dt_txt") String dt_txt; }
    private static class Main { @SerializedName("temp") double temp; }
    private static class Weather {
        public String main;
        @SerializedName("description") String description; @SerializedName("icon") String icon; }
}