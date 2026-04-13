package com.challenge.infrastructure.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SearchIdGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}