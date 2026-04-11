package com.challenge.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record SearchDetailsDto(
        String hotelId,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        List<Integer> ages
) {
    public SearchDetailsDto {
        ages = List.copyOf(ages);
    }
}