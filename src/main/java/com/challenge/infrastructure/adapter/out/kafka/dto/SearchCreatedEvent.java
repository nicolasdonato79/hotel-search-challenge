package com.challenge.infrastructure.adapter.out.kafka.dto;

import java.time.LocalDate;
import java.util.List;

public record SearchCreatedEvent(
        String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {
    public SearchCreatedEvent {
        ages = List.copyOf(ages);
    }
}