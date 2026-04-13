package com.challenge.domain.model;

import java.time.LocalDate;
import java.util.List;

public record SearchCriteria(
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {
    public SearchCriteria {
        ages = List.copyOf(ages);
    }
}