package com.challenge.infrastructure.adapter.out.persistence.mapper;

import com.challenge.domain.model.Search;
import com.challenge.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchPersistenceMapper {

    public SearchEntity toEntity(Search search) {
        return new SearchEntity(
                null,
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                serializeAges(search.ages()),
                LocalDateTime.now()
        );
    }

    public Search toDomain(SearchEntity entity) {
        return new Search(
                entity.getSearchId(),
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                deserializeAges(entity.getAgesSerialized())
        );
    }

    public String serializeAges(List<Integer> ages) {
        return ages.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public List<Integer> deserializeAges(String agesSerialized) {
        return Arrays.stream(agesSerialized.split(","))
                .map(Integer::valueOf)
                .toList();
    }
}