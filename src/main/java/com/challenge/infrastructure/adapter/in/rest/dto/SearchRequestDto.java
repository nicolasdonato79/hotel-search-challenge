package com.challenge.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record SearchRequestDto(

        @NotBlank(message = "hotelId is required")
        String hotelId,

        @NotNull(message = "checkIn is required")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @NotNull(message = "checkOut is required")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        @NotEmpty(message = "ages is required")
        List<@NotNull(message = "age cannot be null")
        @Positive(message = "age must be greater than 0")
                Integer> ages
) {
}