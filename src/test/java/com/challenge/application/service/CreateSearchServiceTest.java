package com.challenge.application.service;

import com.challenge.domain.exception.InvalidSearchException;
import com.challenge.domain.model.Search;
import com.challenge.domain.port.out.SearchEventPublisher;
import com.challenge.domain.port.out.SearchIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSearchServiceTest {

    @Mock
    private SearchEventPublisher searchEventPublisher;

    @Mock
    private SearchIdGenerator searchIdGenerator;

    @InjectMocks
    private CreateSearchService createSearchService;

    @Test
    void shouldCreateSearchAndPublishEvent() {
        Search search = new Search(
                null,
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(searchIdGenerator.generate()).thenReturn("uuid-123");

        String result = createSearchService.create(search);

        assertEquals("uuid-123", result);

        ArgumentCaptor<Search> captor = ArgumentCaptor.forClass(Search.class);
        verify(searchEventPublisher).publish(captor.capture());

        Search published = captor.getValue();
        assertAll(
                () -> assertEquals("uuid-123", result),
                () -> assertEquals("uuid-123", published.searchId()),
                () -> assertEquals("1234aBc", published.hotelId()),
                () -> assertEquals(LocalDate.of(2023, 12, 29), published.checkIn()),
                () -> assertEquals(LocalDate.of(2023, 12, 31), published.checkOut()),
                () -> assertEquals(List.of(30, 29, 1, 3), published.ages())
        );
    }

    @Test
    void shouldThrowExceptionWhenCheckInIsNotBeforeCheckOut() {
        Search search = new Search(
                null,
                "1234aBc",
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2023, 12, 29),
                List.of(30, 29, 1, 3)
        );

        InvalidSearchException ex = assertThrows(
                InvalidSearchException.class,
                () -> createSearchService.create(search)
        );

        assertAll(
                () -> assertEquals("checkIn must be before checkOut", ex.getMessage()),
                () -> verifyNoInteractions(searchIdGenerator),
                () -> verifyNoInteractions(searchEventPublisher)
        );
    }

    @Test
    void shouldThrowExceptionWhenCheckInEqualsCheckOut() {
        Search search = new Search(
                null,
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 29),
                List.of(30, 29, 1, 3)
        );

        assertThrows(InvalidSearchException.class, () -> createSearchService.create(search));
        assertAll(
                () -> verifyNoInteractions(searchIdGenerator),
                () -> verifyNoInteractions(searchEventPublisher)
        );
    }
}