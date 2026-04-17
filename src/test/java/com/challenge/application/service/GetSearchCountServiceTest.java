package com.challenge.application.service;

import com.challenge.domain.exception.SearchNotFoundException;
import com.challenge.domain.model.Search;
import com.challenge.domain.model.SearchCountResult;
import com.challenge.domain.port.out.SearchQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSearchCountServiceTest {

    @Mock
    private SearchQueryRepository searchQueryRepository;

    @InjectMocks
    private GetSearchCountService getSearchCountService;

    @Test
    void shouldReturnSearchAndCount() {
        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(searchQueryRepository.findBySearchId("uuid-123"))
                .thenReturn(Optional.of(search));
        when(searchQueryRepository.countByCriteria(search.toCriteria()))
                .thenReturn(2L);

        SearchCountResult result = getSearchCountService.getBySearchId("uuid-123");

        assertAll(
                () -> assertEquals("uuid-123", result.searchId()),
                () -> assertEquals(search, result.search()),
                () -> assertEquals(2L, result.count()),
                () -> verify(searchQueryRepository).findBySearchId("uuid-123"),
                () -> verify(searchQueryRepository).countByCriteria(search.toCriteria())
        );
    }

    @Test
    void shouldThrowWhenSearchDoesNotExist() {
        when(searchQueryRepository.findBySearchId("missing-uuid"))
                .thenReturn(Optional.empty());

        SearchNotFoundException ex = assertThrows(
                SearchNotFoundException.class,
                () -> getSearchCountService.getBySearchId("missing-uuid")
        );
        assertAll(
                () -> assertEquals("Search not found for searchId: missing-uuid", ex.getMessage()),
                () -> verify(searchQueryRepository).findBySearchId("missing-uuid"),
                () -> verify(searchQueryRepository, never()).countByCriteria(any())
        );
    }
}