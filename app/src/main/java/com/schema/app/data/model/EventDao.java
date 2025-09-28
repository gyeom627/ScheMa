package com.schema.app.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    long insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("DELETE FROM events")
    void deleteAllEvents();

    @Query("SELECT * FROM events WHERE isCompleted = 0 ORDER BY eventTimeMillis ASC")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * FROM events WHERE isCompleted = 1 ORDER BY eventTimeMillis DESC")
    LiveData<List<Event>> getPastEvents();

    @Query("SELECT * FROM events WHERE id = :eventId")
    LiveData<Event> getEventById(int eventId);

    @Query("SELECT * FROM events WHERE id = :eventId")
    Event getEventByIdDirect(int eventId);
}
