package com.example.application.data.repositories;

import com.example.application.data.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfileRepository
        extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {
}

