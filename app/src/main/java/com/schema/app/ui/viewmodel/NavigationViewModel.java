package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

/**
 * NavigationActivity에서 사용되는 ViewModel입니다.
 * 특정 이벤트 정보를 불러오는 역할을 합니다.
 */
public class NavigationViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final LiveData<Event> event;

    public NavigationViewModel(@NonNull Application application, int eventId) {
        super(application);
        repository = new EventRepository(application);
        event = repository.getEventById(eventId);
    }

    public LiveData<Event> getEvent() {
        return event;
    }
}
