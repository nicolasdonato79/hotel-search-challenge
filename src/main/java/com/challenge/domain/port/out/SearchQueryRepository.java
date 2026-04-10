package com.challenge.domain.port.out;

import com.challenge.domain.model.Search;
import com.challenge.domain.model.SearchCriteria;

import java.util.Optional;

public interface SearchQueryRepository {
    Optional<Search> findBySearchId(String searchId);
    long countByCriteria(SearchCriteria criteria);
}