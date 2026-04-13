package com.challenge.domain.exception;

public class SearchNotFoundException extends RuntimeException {

    public SearchNotFoundException(String searchId) {
        super("Search not found for searchId: " + searchId);
    }
}