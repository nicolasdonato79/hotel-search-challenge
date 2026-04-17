package com.challenge.application.port.in;

import com.challenge.domain.model.Search;

public interface CreateSearchUseCase {
    String create(Search search);
}