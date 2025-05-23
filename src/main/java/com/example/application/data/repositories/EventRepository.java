package com.example.application.data.repositories;

import com.example.application.data.entities.Event;
import com.example.application.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventRepository
        extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByOwner(User owner);

}

