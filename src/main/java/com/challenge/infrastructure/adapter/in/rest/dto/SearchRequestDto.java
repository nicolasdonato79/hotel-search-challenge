package com.challenge.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(example = "29/12/2023")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @NotNull(message = "checkOut is required")
        @Schema(example = "31/12/2023")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        @Schema(example = "[30, 29, 1, 3]")
        @NotEmpty(message = "ages is required")
        List<@NotNull(message = "age cannot be null")
        @Positive(message = "age must be greater than 0")
                Integer> ages
) {
}