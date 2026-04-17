package com.challenge.domain.model;

import java.time.LocalDate;
import java.util.List;

public record Search(
        String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages
) {

    public Search {
        ages = List.copyOf(ages);
    }

    public SearchCriteria toCriteria() {
        return new SearchCriteria(
                hotelId,
                checkIn,
                checkOut,
                ages
        );
    }
}