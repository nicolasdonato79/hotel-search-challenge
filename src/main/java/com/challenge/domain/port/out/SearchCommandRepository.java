package com.challenge.domain.port.out;

import com.challenge.domain.model.Search;

public interface SearchCommandRepository {
    void save(Search search);
}