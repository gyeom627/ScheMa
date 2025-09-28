package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

public class ArrivalViewModel extends AndroidViewModel {

    private final EventRepository repository;

    public ArrivalViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
    }

    public LiveData<Event> getEventById(int eventId) {
        return repository.getEventById(eventId);
    }

    public void update(Event event) {
        repository.update(event);
    }
}
