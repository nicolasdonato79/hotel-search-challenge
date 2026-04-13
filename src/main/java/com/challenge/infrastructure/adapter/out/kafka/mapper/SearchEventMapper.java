package com.challenge.infrastructure.adapter.out.kafka.mapper;

import com.challenge.domain.model.Search;
import com.challenge.infrastructure.adapter.out.kafka.dto.SearchCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class SearchEventMapper {

    public SearchCreatedEvent toEvent(Search search) {
        return new SearchCreatedEvent(
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages()
        );
    }

    public Search toDomain(SearchCreatedEvent event) {
        return new Search(
                event.searchId(),
                event.hotelId(),
                event.checkIn(),
                event.checkOut(),
                event.ages()
        );
    }
}