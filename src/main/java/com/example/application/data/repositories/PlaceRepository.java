package com.example.application.data.repositories;

import com.example.application.data.entities.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlaceRepository
        extends JpaRepository<Place, Long>, JpaSpecificationExecutor<Place> {
}

