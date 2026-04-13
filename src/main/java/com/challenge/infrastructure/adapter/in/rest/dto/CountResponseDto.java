package com.challenge.infrastructure.adapter.in.rest.dto;

public record CountResponseDto(
        String searchId,
        SearchDetailsDto search,
        long count
) {
}