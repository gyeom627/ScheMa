package com.schema.app.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.schema.app.data.model.Event;
import com.schema.app.data.repository.EventRepository;

import java.util.List;

public class PastEventsViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final LiveData<List<Event>> pastEvents;

    public PastEventsViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
        pastEvents = repository.getPastEvents();
    }

    public LiveData<List<Event>> getPastEvents() {
        return pastEvents;
    }
}
