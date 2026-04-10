package com.challenge.domain.port.out;

import com.challenge.domain.model.Search;

public interface SearchEventPublisher {
    void publish(Search search);
}