package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.schema.app.ApiClient;
import com.schema.app.BuildConfig;
import com.schema.app.NaverDirectionsApiService;
import com.schema.app.NaverDirectionsResponse;
import com.schema.app.OpenWeatherMapApiService;
import com.schema.app.WeatherResponse;
import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AdjustAlarmActivity에서 사용되는 ViewModel입니다.
 * 최종 알람 시간을 계산하고, 이동 시간 및 날씨 정보를 API를 통해 가져오는 비즈니스 로직을 처리합니다.
 */
public class AdjustAlarmViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final MutableLiveData<Integer> travelTimeMinutes = new MutableLiveData<>();
    private final MutableLiveData<String> weatherInfo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AdjustAlarmViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
    }

    /**
     * API를 통해 계산된 예상 이동 시간(분)을 LiveData 형태로 반환합니다.
     * @return 예상 이동 시간을 담는 LiveData
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#setupObservers()
     */
    public LiveData<Integer> getTravelTimeMinutes() {
        return travelTimeMinutes;
    }

    /**
     * API를 통해 가져온 날씨 정보를 LiveData 형태로 반환합니다.
     * @return 날씨 정보 문자열을 담는 LiveData
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#setupObservers()
     */
    public LiveData<String> getWeatherInfo() {
        return weatherInfo;
    }

    /**
     * API 호출 시 로딩 상태를 LiveData 형태로 반환합니다.
     * @return 로딩 상태(true/false)를 담는 LiveData
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#setupObservers()
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * 특정 ID를 가진 Event 객체를 데이터베이스에서 가져옵니다. (수정 모드용)
     * @param eventId 가져올 이벤트의 ID
     * @return Event 객체를 담는 LiveData
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#saveFinalEvent()
     */
    public LiveData<Event> getEventById(int eventId) {
        return repository.getEventById(eventId);
    }

    /**
     * 새로운 이벤트를 데이터베이스에 삽입합니다.
     * @param event 삽입할 Event 객체
     * @param callback 삽입 완료 후, 생성된 ID를 반환받는 콜백
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#saveFinalEvent()
     */
    public void insert(Event event, Consumer<Long> callback) {
        repository.insert(event, callback);
    }

    /**
     * 기존 이벤트를 데이터베이스에서 업데이트합니다.
     * @param event 업데이트할 Event 객체
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#saveFinalEvent()
     */
    public void update(Event event) {
        repository.update(event);
    }

    /**
     * Naver Directions API를 호출하여 자동차 경로의 예상 이동 시간을 계산합니다.
     * @param homeLat 출발지 위도
     * @param homeLng 출발지 경도
     * @param destLat 도착지 위도
     * @param destLng 도착지 경도
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#onCreate(Bundle)
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#setupAdjustmentListeners()
     */
    public void calculateTravelTime(double homeLat, double homeLng, double destLat, double destLng) {
        isLoading.setValue(true);
        String start = homeLng + "," + homeLat;
        String goal = destLat + "," + destLng;

        NaverDirectionsApiService service = ApiClient.getDirectionsApiService(BuildConfig.NAVER_MAPS_CLIENT_ID, BuildConfig.NAVER_MAPS_CLIENT_SECRET);
        Call<NaverDirectionsResponse> call = service.getDrivingRoute(start, goal);

        call.enqueue(new Callback<NaverDirectionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NaverDirectionsResponse> call, @NonNull Response<NaverDirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getRoute() != null &&
                        response.body().getRoute().getTrafast() != null && !response.body().getRoute().getTrafast().isEmpty()) {

                    long travelTimeMillis = response.body().getRoute().getTrafast().get(0).getSummary().getDuration();
                    travelTimeMinutes.setValue((int) TimeUnit.MILLISECONDS.toMinutes(travelTimeMillis));
                } else {
                    travelTimeMinutes.setValue(0);
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<NaverDirectionsResponse> call, @NonNull Throwable t) {
                travelTimeMinutes.setValue(0);
                isLoading.setValue(false);
            }
        });
    }

    /**
     * OpenWeatherMap API를 호출하여 약속 장소의 날씨 예보를 가져옵니다.
     * @param lat 도착지 위도
     * @param lon 도착지 경도
     * @param appointmentTimeMillis 약속 시간 (가장 가까운 시간대의 예보를 찾는데 사용)
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#onCreate(Bundle)
     */
    public void fetchWeatherInfo(double lat, double lon, long appointmentTimeMillis) {
        // TODO: API 키를 받은 후 아래 주석을 해제하세요.
        /*
        OpenWeatherMapApiService service = ApiClient.getWeatherApiService();
        Call<WeatherResponse> call = service.getForecast(lat, lon, BuildConfig.WEATHER_API_KEY, "metric", "kr");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().list != null) {
                    WeatherResponse.Forecast closestForecast = null;
                    long minTimeDiff = Long.MAX_VALUE;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                    for (WeatherResponse.Forecast forecast : response.body().list) {
                        try {
                            Date forecastDate = sdf.parse(forecast.dt_txt);
                            if (forecastDate != null) {
                                long timeDiff = Math.abs(appointmentTimeMillis - forecastDate.getTime());
                                if (timeDiff < minTimeDiff) {
                                    minTimeDiff = timeDiff;
                                    closestForecast = forecast;
                                }
                            }
                        } catch (ParseException e) {
                            // Log error
                        }
                    }

                    if (closestForecast != null && !closestForecast.weather.isEmpty()) {
                        String weatherDescription = closestForecast.weather.get(0).description;
                        weatherInfo.setValue("예상 날씨: " + weatherDescription);
                    } else {
                        weatherInfo.setValue("예상 날씨: 정보 없음");
                    }
                } else {
                    weatherInfo.setValue("예상 날씨: 정보 없음");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherInfo.setValue("예상 날씨: 정보를 가져올 수 없음");
            }
        });
        */
        // 임시 데이터
        weatherInfo.setValue("예상 날씨: (API 비활성화)");
    }
}