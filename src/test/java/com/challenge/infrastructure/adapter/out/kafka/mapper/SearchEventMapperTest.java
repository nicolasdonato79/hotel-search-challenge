package com.challenge.infrastructure.adapter.out.kafka.mapper;

import com.challenge.domain.model.Search;
import com.challenge.infrastructure.adapter.out.kafka.dto.SearchCreatedEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchEventMapperTest {

    private final SearchEventMapper mapper = new SearchEventMapper();

    @Test
    void shouldMapSearchToEvent() {
        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        SearchCreatedEvent event = mapper.toEvent(search);

        assertEquals("uuid-123", event.searchId());
        assertEquals("1234aBc", event.hotelId());
        assertEquals(LocalDate.of(2023, 12, 29), event.checkIn());
        assertEquals(LocalDate.of(2023, 12, 31), event.checkOut());
        assertEquals(List.of(30, 29, 1, 3), event.ages());
    }

    @Test
    void shouldMapEventToSearch() {
        SearchCreatedEvent event = new SearchCreatedEvent(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        Search search = mapper.toDomain(event);

        assertEquals("uuid-123", search.searchId());
        assertEquals("1234aBc", search.hotelId());
        assertEquals(LocalDate.of(2023, 12, 29), search.checkIn());
        assertEquals(LocalDate.of(2023, 12, 31), search.checkOut());
        assertEquals(List.of(30, 29, 1, 3), search.ages());
    }
}