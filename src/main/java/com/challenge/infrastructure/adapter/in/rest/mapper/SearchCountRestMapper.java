package com.challenge.infrastructure.adapter.in.rest.mapper;

import com.challenge.domain.model.Search;
import com.challenge.domain.model.SearchCountResult;
import com.challenge.infrastructure.adapter.in.rest.dto.CountResponseDto;
import com.challenge.infrastructure.adapter.in.rest.dto.SearchDetailsDto;
import org.springframework.stereotype.Component;

@Component
public class SearchCountRestMapper {

    public CountResponseDto toDto(SearchCountResult result) {
        Search search = result.search();

        SearchDetailsDto searchDto = new SearchDetailsDto(
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages()
        );

        return new CountResponseDto(
                result.searchId(),
                searchDto,
                result.count()
        );
    }
}