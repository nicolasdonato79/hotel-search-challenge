package com.challenge.infrastructure.adapter.out.persistence.mapper;

import com.challenge.domain.model.Search;
import com.challenge.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchPersistenceMapperTest {

    private final SearchPersistenceMapper mapper = new SearchPersistenceMapper();

    @Test
    void shouldSerializeAndDeserializeAges() {
        String serialized = mapper.serializeAges(List.of(30, 29, 1, 3));
        assertEquals("30,29,1,3", serialized);

        List<Integer> ages = mapper.deserializeAges(serialized);
        assertEquals(List.of(30, 29, 1, 3), ages);
    }

    @Test
    void shouldMapSearchToEntity() {
        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        SearchEntity entity = mapper.toEntity(search);

        assertEquals("uuid-123", entity.getSearchId());
        assertEquals("1234aBc", entity.getHotelId());
        assertEquals(LocalDate.of(2023, 12, 29), entity.getCheckIn());
        assertEquals(LocalDate.of(2023, 12, 31), entity.getCheckOut());
        assertEquals("30,29,1,3", entity.getAgesSerialized());
    }

    @Test
    void shouldMapEntityToSearch() {
        SearchEntity entity = new SearchEntity(
                1L,
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                "30,29,1,3",
                null
        );

        Search search = mapper.toDomain(entity);

        assertEquals("uuid-123", search.searchId());
        assertEquals("1234aBc", search.hotelId());
        assertEquals(LocalDate.of(2023, 12, 29), search.checkIn());
        assertEquals(LocalDate.of(2023, 12, 31), search.checkOut());
        assertEquals(List.of(30, 29, 1, 3), search.ages());
    }
}