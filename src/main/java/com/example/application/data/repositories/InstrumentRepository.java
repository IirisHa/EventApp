package com.example.application.data.repositories;

import com.example.application.data.entities.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstrumentRepository
        extends JpaRepository<Instrument, Long>, JpaSpecificationExecutor<Instrument> {
}


