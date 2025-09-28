package com.schema.app.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

/**
 * EventDetailActivity에서 사용되는 ViewModel입니다.
 * 특정 이벤트의 상세 정보를 불러오고, 해당 이벤트를 삭제하는 역할을 합니다.
 */
public class EventDetailViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final LiveData<Event> event;

    public EventDetailViewModel(@NonNull Application application, int eventId) {
        super(application);
        repository = new EventRepository(application);
        // 생성 시 전달받은 eventId를 사용하여 특정 이벤트를 데이터베이스에서 가져옵니다.
        event = repository.getEventById(eventId);
    }

    /**
     * 데이터베이스에서 가져온 특정 이벤트 정보를 LiveData 형태로 반환합니다.
     * @return Event 객체를 담는 LiveData
     * @see com.schema.app.ui.activity.EventDetailActivity#onCreate(Bundle)
     */
    public LiveData<Event> getEvent() {
        return event;
    }

    /**
     * 특정 이벤트를 데이터베이스에서 삭제합니다.
     * @param event 삭제할 Event 객체
     * @see com.schema.app.ui.activity.EventDetailActivity#onCreate(Bundle)
     */
    public void delete(Event event) {
        repository.delete(event);
    }
}