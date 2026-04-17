package com.challenge.infrastructure.adapter.in.kafka;

import com.challenge.domain.model.Search;
import com.challenge.domain.port.out.SearchCommandRepository;
import com.challenge.infrastructure.adapter.out.kafka.dto.SearchCreatedEvent;
import com.challenge.infrastructure.adapter.out.kafka.mapper.SearchEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchKafkaConsumerTest {

    @Mock
    private SearchCommandRepository searchCommandRepository;

    @Mock
    private SearchEventMapper searchEventMapper;

    @Mock
    private ExecutorService virtualThreadExecutor;

    @InjectMocks
    private SearchKafkaConsumer searchKafkaConsumer;

    @Test
    void shouldConsumeEventAndPersistSearch() {
        SearchCreatedEvent event = new SearchCreatedEvent(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(searchEventMapper.toDomain(event)).thenReturn(search);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        when(virtualThreadExecutor.submit(runnableCaptor.capture())).thenReturn(mock(Future.class));

        searchKafkaConsumer.consume(event);

        Runnable runnable = runnableCaptor.getValue();
        assertNotNull(runnable);

        runnable.run();

        assertAll(
                () -> verify(virtualThreadExecutor).submit(any(Runnable.class)),
                () -> verify(searchEventMapper).toDomain(event),
                () -> verify(searchCommandRepository).save(search)
        );
    }
}