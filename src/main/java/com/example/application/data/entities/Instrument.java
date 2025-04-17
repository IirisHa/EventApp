package com.example.application.data.entities;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Instrument extends AbstractEntity {

    private String name;

    @ManyToMany(mappedBy = "instruments")
    private List<Event> events;

    public Instrument() {}

    public Instrument(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}

