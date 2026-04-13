package com.challenge.infrastructure.adapter.out.persistence.adapter;

import com.challenge.domain.model.Search;
import com.challenge.domain.model.SearchCriteria;
import com.challenge.infrastructure.adapter.out.persistence.entity.SearchEntity;
import com.challenge.infrastructure.adapter.out.persistence.mapper.SearchPersistenceMapper;
import com.challenge.infrastructure.adapter.out.persistence.repository.SpringDataSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchPersistenceAdapterTest {

    @Mock
    private SpringDataSearchRepository repository;

    @Mock
    private SearchPersistenceMapper mapper;

    @InjectMocks
    private SearchPersistenceAdapter adapter;

    @Test
    void shouldSaveSearch() {
        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        SearchEntity entity = new SearchEntity(
                1L,
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                "30,29,1,3",
                LocalDateTime.now()
        );

        when(mapper.toEntity(search)).thenReturn(entity);

        adapter.save(search);

        verify(mapper).toEntity(search);
        verify(repository).save(entity);
    }

    @Test
    void shouldFindSearchBySearchId() {
        SearchEntity entity = new SearchEntity(
                1L,
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                "30,29,1,3",
                LocalDateTime.now()
        );

        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(repository.findBySearchId("uuid-123")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(search);

        Optional<Search> result = adapter.findBySearchId("uuid-123");

        assertTrue(result.isPresent());
        assertEquals("uuid-123", result.get().searchId());
        verify(repository).findBySearchId("uuid-123");
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenSearchIdDoesNotExist() {
        when(repository.findBySearchId("missing")).thenReturn(Optional.empty());

        Optional<Search> result = adapter.findBySearchId("missing");

        assertTrue(result.isEmpty());
        verify(repository).findBySearchId("missing");
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldCountByCriteria() {
        SearchCriteria criteria = new SearchCriteria(
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(mapper.serializeAges(criteria.ages())).thenReturn("30,29,1,3");
        when(repository.countByHotelIdAndCheckInAndCheckOutAndAgesSerialized(
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                "30,29,1,3"
        )).thenReturn(2L);

        long result = adapter.countByCriteria(criteria);

        assertEquals(2L, result);
        verify(mapper).serializeAges(criteria.ages());
    }
}