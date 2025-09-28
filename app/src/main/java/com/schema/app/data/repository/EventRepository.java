package com.schema.app.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.schema.app.data.model.AppDatabase;
import com.schema.app.data.model.Event;
import com.schema.app.data.model.EventDao;

import java.util.List;
import java.util.function.Consumer;

public class EventRepository {

    private final EventDao mEventDao;
    private final LiveData<List<Event>> mAllEvents;

    public EventRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mEventDao = db.eventDao();
        mAllEvents = mEventDao.getAllEvents();
    }

    public LiveData<List<Event>> getAllEvents() {
        return mAllEvents;
    }

    public LiveData<List<Event>> getPastEvents() {
        return mEventDao.getPastEvents();
    }

    public LiveData<Event> getEventById(int eventId) {
        return mEventDao.getEventById(eventId);
    }

    public Event getEventByIdDirect(int eventId) {
        return mEventDao.getEventByIdDirect(eventId);
    }

    public void insert(Event event, Consumer<Long> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = mEventDao.insert(event);
            callback.accept(id);
        });
    }

    public void update(Event event) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mEventDao.update(event);
        });
    }

    public void delete(Event event) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mEventDao.delete(event);
        });
    }
}