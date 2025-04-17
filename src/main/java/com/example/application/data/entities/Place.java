package com.example.application.data.entities;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Place extends AbstractEntity {

    private String name;
    private String address;
    private String city;

    @OneToMany(mappedBy = "place")
    private List<Event> events;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
