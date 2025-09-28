package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.schema.app.ApiClient;
import com.schema.app.BuildConfig;
import com.schema.app.OpenWeatherMapApiService;
import com.schema.app.WeatherResponse;
import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PreparationActivity에서 사용되는 ViewModel입니다.
 * 특정 이벤트 정보를 불러오고, 해당 이벤트의 날씨 정보를 가져오는 역할을 합니다.
 */
public class PreparationViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final LiveData<Event> event;
    private final MutableLiveData<String> weatherInfo = new MutableLiveData<>();

    public PreparationViewModel(@NonNull Application application, int eventId) {
        super(application);
        repository = new EventRepository(application);
        event = repository.getEventById(eventId);
    }

    /**
     * ID에 해당하는 이벤트 정보를 LiveData 형태로 반환합니다.
     * @return Event 객체를 담는 LiveData
     */
    public LiveData<Event> getEvent() {
        return event;
    }

    /**
     * 날씨 정보를 LiveData 형태로 반환합니다.
     * @return 날씨 정보 문자열을 담는 LiveData
     */
    public LiveData<String> getWeatherInfo() {
        return weatherInfo;
    }

    /**
     * OpenWeatherMap API를 호출하여 날씨 예보를 가져옵니다.
     * @param event 날씨를 조회할 대상 이벤트
     */
    public void fetchWeatherInfo(Event event) {
        if (event == null) return;

        double lat = event.getLatitude();
        double lon = event.getLongitude();
        long appointmentTimeMillis = event.getEventTimeMillis();

        // TODO: API 키를 받은 후 아래 주석을 해제하세요.
        /*
        OpenWeatherMapApiService service = ApiClient.getWeatherApiService();
        Call<WeatherResponse> call = service.getForecast(lat, lon, BuildConfig.WEATHER_API_KEY, "metric", "kr");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().list != null) {
                    // ... (AdjustAlarmViewModel과 동일한 날씨 파싱 로직) ...
                    weatherInfo.setValue("예상 날씨: " + weatherDescription);
                } else {
                    weatherInfo.setValue("날씨 정보 없음");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherInfo.setValue("날씨 정보 로드 실패");
            }
        });
        */
        weatherInfo.setValue("맑음 (임시)"); // 임시 데이터
    }
}
