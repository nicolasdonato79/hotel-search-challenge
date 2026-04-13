package com.challenge.infrastructure.adapter.in.rest.mapper;

import com.challenge.domain.model.Search;
import com.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDto;
import org.springframework.stereotype.Component;

@Component
public class SearchRestMapper {

    public Search toDomain(SearchRequestDto dto) {
        return new Search(
                null,
                dto.hotelId(),
                dto.checkIn(),
                dto.checkOut(),
                dto.ages()
        );
    }
}