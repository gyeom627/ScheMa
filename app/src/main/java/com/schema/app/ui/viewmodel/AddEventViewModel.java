package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.schema.app.ApiClient;
import com.schema.app.BuildConfig;
import com.schema.app.GovAddressApiService;
import com.schema.app.GovAddressResponse;
import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AddEventActivity와 EditEventActivity에서 사용되는 ViewModel입니다.
 * 일정 추가, 수정, 주소 검색 등의 비즈니스 로직을 처리합니다.
 */
public class AddEventViewModel extends AndroidViewModel {

    private final EventRepository mRepository;
    private final MutableLiveData<List<String>> addressSuggestions = new MutableLiveData<>();
    private final MutableLiveData<List<GovAddressResponse.Juso>> jusoList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AddEventViewModel(Application application) {
        super(application);
        mRepository = new EventRepository(application);
    }

    /**
     * 주소 검색 자동완성 제안 목록을 LiveData 형태로 반환합니다.
     * @return 주소 문자열 목록을 담는 LiveData
     * @see AddEventActivity#setupObservers()
     */
    public LiveData<List<String>> getAddressSuggestions() {
        return addressSuggestions;
    }

    /**
     * 주소 검색 결과(Juso 객체) 목록을 LiveData 형태로 반환합니다.
     * @return Juso 객체 목록을 담는 LiveData
     * @see AddEventActivity#setupObservers()
     */
    public LiveData<List<GovAddressResponse.Juso>> getJusoList() {
        return jusoList;
    }

    /**
     * 주소 검색 API 호출 시 로딩 상태를 LiveData 형태로 반환합니다.
     * @return 로딩 상태(true/false)를 담는 LiveData
     * @see AddEventActivity#setupObservers()
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * 특정 ID를 가진 Event 객체를 데이터베이스에서 가져옵니다.
     * @param eventId 가져올 이벤트의 ID
     * @return Event 객체를 담는 LiveData
     * @see com.schema.app.ui.activity.AddEventActivity#loadEventForEdit(int)
     */
    public LiveData<Event> getEventById(int eventId) {
        return mRepository.getEventById(eventId);
    }

    /**
     * 새로운 이벤트를 데이터베이스에 삽입합니다.
     * @param event 삽입할 Event 객체
     * @param callback 삽입 완료 후, 생성된 ID를 반환받는 콜백
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#saveFinalEvent()
     */
    public void insert(Event event, Consumer<Long> callback) {
        mRepository.insert(event, callback);
    }

    /**
     * 기존 이벤트를 데이터베이스에서 업데이트합니다.
     * @param event 업데이트할 Event 객체
     * @see com.schema.app.ui.activity.AdjustAlarmActivity#saveFinalEvent()
     */
    public void update(Event event) {
        mRepository.update(event);
    }

    /**
     * 정부 주소 API를 호출하여 주소를 검색합니다.
     * 검색 결과는 addressSuggestions와 jusoList LiveData를 통해 UI에 전달됩니다.
     * @param query 검색할 주소 문자열
     * @see com.schema.app.ui.activity.AddEventActivity#setupClickListeners()
     */
    public void searchAddress(String query) {
        isLoading.setValue(true);

        // TODO: API 키를 받은 후 아래 주석을 해제하세요.
        /*
        GovAddressApiService service = ApiClient.getGovAddressApiService();
        String apiKey = BuildConfig.GOV_ADDRESS_API_KEY;

        Call<GovAddressResponse> call = service.searchAddress(apiKey, 1, 20, query, "json");

        call.enqueue(new Callback<GovAddressResponse>() {
            @Override
            public void onResponse(Call<GovAddressResponse> call, Response<GovAddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().results != null && response.body().results.juso != null) {
                    List<GovAddressResponse.Juso> jusos = response.body().results.juso;
                    jusoList.setValue(jusos);
                    addressSuggestions.setValue(jusos.stream().map(juso -> juso.roadAddr).collect(Collectors.toList()));
                } else {
                    jusoList.setValue(java.util.Collections.emptyList());
                    addressSuggestions.setValue(java.util.Collections.emptyList());
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<GovAddressResponse> call, Throwable t) {
                isLoading.setValue(false);
                // 오류 처리
            }
        });
        */

        // 임시 더미 데이터 (API 키 없을 때 테스트용)
         isLoading.setValue(false);
    }
}