package com.challenge.infrastructure.adapter.out.persistence.repository;

import com.challenge.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataSearchRepository extends JpaRepository<SearchEntity, Long> {

    Optional<SearchEntity> findBySearchId(String searchId);

    //Evito inyección.
    long countByHotelIdAndCheckInAndCheckOutAndAgesSerialized(
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            String agesSerialized
    );
}