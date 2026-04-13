package com.challenge.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_search", indexes = {
                @Index(
                        name = "idx_hotel_search_count",
                        columnList = "hotel_id,check_in,check_out,ages_serialized"
                )
        },
        uniqueConstraints = {@UniqueConstraint(
                        name = "uk_hotel_search_search_id",
                        columnNames = "search_id"
                )
        }
)
public class SearchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "search_id", nullable = false, length = 36)
    private String searchId;

    @Column(name = "hotel_id", nullable = false, length = 50)
    private String hotelId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "ages_serialized", nullable = false, length = 255)
    private String agesSerialized;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected SearchEntity() {
    }

    public SearchEntity(Long id,
                        String searchId,
                        String hotelId,
                        LocalDate checkIn,
                        LocalDate checkOut,
                        String agesSerialized,
                        LocalDateTime createdAt) {
        this.id = id;
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.agesSerialized = agesSerialized;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getSearchId() {
        return searchId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getAgesSerialized() {
        return agesSerialized;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}