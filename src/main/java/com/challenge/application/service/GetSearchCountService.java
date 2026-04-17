package com.challenge.application.service;

import com.challenge.domain.exception.SearchNotFoundException;
import com.challenge.domain.model.Search;
import com.challenge.domain.model.SearchCountResult;
import com.challenge.application.port.in.GetSearchCountUseCase;
import com.challenge.domain.port.out.SearchQueryRepository;

public class GetSearchCountService implements GetSearchCountUseCase {

    private final SearchQueryRepository searchQueryRepository;

    public GetSearchCountService(SearchQueryRepository searchQueryRepository) {
        this.searchQueryRepository = searchQueryRepository;
    }

    @Override
    public SearchCountResult getBySearchId(String searchId) {
        Search search = searchQueryRepository.findBySearchId(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId));

        long count = searchQueryRepository.countByCriteria(search.toCriteria());

        return new SearchCountResult(searchId, search, count);
    }
}