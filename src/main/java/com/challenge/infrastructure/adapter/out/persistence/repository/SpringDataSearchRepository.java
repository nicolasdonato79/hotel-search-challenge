package com.challenge.infrastructure.adapter.out.persistence.repository;

import com.challenge.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataSearchRepository extends JpaRepository<SearchEntity, Long> {

    Optional<SearchEntity> findBySearchId(String searchId);

    //Tuve que hacerlo asi por el conflicto IN al ser palabra reservada
    @Query("""
            SELECT COUNT(s) FROM SearchEntity s  
            WHERE s.hotelId = :hotelId  
            AND s.checkIn = :checkIn 
            AND s.checkOut = :checkOut  
            AND s.agesSerialized = :agesSerialized 
            """)
    long countByHotelIdAndCheckInAndCheckOutAndAgesSerialized(
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            String agesSerialized
    );
}