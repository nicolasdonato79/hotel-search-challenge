package com.challenge.domain.model;

public record SearchCountResult(
        String searchId,
        Search search,
        long count
) {
}