package com.schema.app.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private long eventTimeMillis;
    private long preparationTimeMillis;
    private long travelTimeMillis;
    private String travelMode;
    private String address;
    private double latitude;
    private double longitude;
    private int notificationId;
    private long preparationNotificationTimeMillis;
    private long startPreparationTimeMillis;
    private long actualPreparationTimeMillis;
    private long actualTravelTimeMillis;
    private boolean isCompleted;

    public Event(String title, long eventTimeMillis, long preparationTimeMillis, long travelTimeMillis, String travelMode, String address, double latitude, double longitude, int notificationId, long preparationNotificationTimeMillis, long startPreparationTimeMillis, long actualPreparationTimeMillis, long actualTravelTimeMillis, boolean isCompleted) {
        this.title = title;
        this.eventTimeMillis = eventTimeMillis;
        this.preparationTimeMillis = preparationTimeMillis;
        this.travelTimeMillis = travelTimeMillis;
        this.travelMode = travelMode;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notificationId = notificationId;
        this.preparationNotificationTimeMillis = preparationNotificationTimeMillis;
        this.startPreparationTimeMillis = startPreparationTimeMillis;
        this.actualPreparationTimeMillis = actualPreparationTimeMillis;
        this.actualTravelTimeMillis = actualTravelTimeMillis;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public long getEventTimeMillis() { return eventTimeMillis; }
    public void setEventTimeMillis(long eventTimeMillis) { this.eventTimeMillis = eventTimeMillis; }
    public long getPreparationTimeMillis() { return preparationTimeMillis; }
    public void setPreparationTimeMillis(long preparationTimeMillis) { this.preparationTimeMillis = preparationTimeMillis; }
    public long getTravelTimeMillis() { return travelTimeMillis; }
    public void setTravelTimeMillis(long travelTimeMillis) { this.travelTimeMillis = travelTimeMillis; }
    public String getTravelMode() { return travelMode; }
    public void setTravelMode(String travelMode) { this.travelMode = travelMode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
    public long getPreparationNotificationTimeMillis() { return preparationNotificationTimeMillis; }
    public void setPreparationNotificationTimeMillis(long preparationNotificationTimeMillis) { this.preparationNotificationTimeMillis = preparationNotificationTimeMillis; }
    public long getStartPreparationTimeMillis() { return startPreparationTimeMillis; }
    public void setStartPreparationTimeMillis(long startPreparationTimeMillis) { this.startPreparationTimeMillis = startPreparationTimeMillis; }
    public long getActualPreparationTimeMillis() { return actualPreparationTimeMillis; }
    public void setActualPreparationTimeMillis(long actualPreparationTimeMillis) { this.actualPreparationTimeMillis = actualPreparationTimeMillis; }
    public long getActualTravelTimeMillis() { return actualTravelTimeMillis; }
    public void setActualTravelTimeMillis(long actualTravelTimeMillis) { this.actualTravelTimeMillis = actualTravelTimeMillis; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
