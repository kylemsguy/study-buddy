package com.kylemsguy.studybuddy;

import android.app.Application;
import com.google.api.services.calendar.model.Event;

import java.util.List;

/**
 * Created by kyle on 01/02/15.
 */
public class SBApp extends Application {
    private List<Event> events = null;

    private String username = null;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double latitude = 0;
    private double longitude = 0;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email = null;

    public SBApp(){
        super();
    }

    public void setEvents(List<Event> events){
        this.events = events;
    }

    public List<Event> getEvents(){
        return this.events;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
