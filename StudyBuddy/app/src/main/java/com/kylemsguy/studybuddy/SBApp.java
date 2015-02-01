package com.kylemsguy.studybuddy;

import android.app.Application;
import com.google.api.services.calendar.model.Event;

import java.util.List;

/**
 * Created by kyle on 01/02/15.
 */
public class SBApp extends Application {
    private List<Event> events = null;

    public SBApp(){
        super();
    }

    public void setEvents(List<Event> events){
        this.events = events;
    }

    public List<Event> getEvents(){
        return this.events;
    }

}
