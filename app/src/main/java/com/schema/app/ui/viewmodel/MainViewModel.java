package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

import java.util.List;

/**
 * MainActivity에서 사용되는 ViewModel입니다.
 * 데이터베이스에 저장된 모든 일정 정보를 가져오는 역할을 합니다.
 */
public class MainViewModel extends AndroidViewModel {

    private final EventRepository mRepository;
    private final LiveData<List<Event>> mAllEvents;

    public MainViewModel(Application application) {
        super(application);
        mRepository = new EventRepository(application);
        mAllEvents = mRepository.getAllEvents();
    }

    /**
     * 데이터베이스에 저장된 모든 일정을 LiveData 형태로 반환합니다.
     * UI(MainActivity)는 이 LiveData를 관찰하여 일정 목록의 변경을 감지하고 화면을 업데이트합니다.
     * @return 모든 Event 객체 목록을 담는 LiveData
     * @see com.schema.app.ui.activity.MainActivity#onCreate(Bundle)
     */
    public LiveData<List<Event>> getAllEvents() {
        return mAllEvents;
    }
}