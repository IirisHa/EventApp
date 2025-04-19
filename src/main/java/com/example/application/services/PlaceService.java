package com.example.application.services;

import com.example.application.data.entities.Place;
import com.example.application.data.repositories.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    public Place save(Place place) {
        return placeRepository.save(place);
    }

    public void delete(Place place) {
        placeRepository.delete(place);
    }

    public Place findById(Long id) {
        return placeRepository.findById(id).orElse(null);
    }
}
