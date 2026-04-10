package com.challenge.application.service;

import com.challenge.domain.exception.InvalidSearchException;
import com.challenge.domain.model.Search;
import com.challenge.domain.port.in.CreateSearchUseCase;
import com.challenge.domain.port.out.SearchEventPublisher;
import com.challenge.infrastructure.util.SearchIdGenerator;
import org.springframework.stereotype.Service;

@Service
public class CreateSearchService implements CreateSearchUseCase {

    private final SearchEventPublisher searchEventPublisher;
    private final SearchIdGenerator searchIdGenerator;

    public CreateSearchService(SearchEventPublisher searchEventPublisher,
                               SearchIdGenerator searchIdGenerator) {
        this.searchEventPublisher = searchEventPublisher;
        this.searchIdGenerator = searchIdGenerator;
    }

    @Override
    public String create(Search search) {
        validateBusinessRules(search);

        Search searchWithId = new Search(
                searchIdGenerator.generate(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages()
        );

        searchEventPublisher.publish(searchWithId);

        return searchWithId.searchId();
    }

    private void validateBusinessRules(Search search) {
        if (!search.checkIn().isBefore(search.checkOut())) {
            throw new InvalidSearchException("checkIn must be before checkOut");
        }
    }
}