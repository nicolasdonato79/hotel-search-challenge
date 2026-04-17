package com.challenge.infrastructure.adapter.out.kafka;

import com.challenge.domain.model.Search;
import com.challenge.infrastructure.adapter.out.kafka.dto.SearchCreatedEvent;
import com.challenge.infrastructure.adapter.out.kafka.mapper.SearchEventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, SearchCreatedEvent> kafkaTemplate;

    @Mock
    private SearchEventMapper searchEventMapper;

    @InjectMocks
    private SearchKafkaProducer searchKafkaProducer;

    @Test
    void shouldPublishEventToKafka() {
        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        SearchCreatedEvent event = new SearchCreatedEvent(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(searchEventMapper.toEvent(search)).thenReturn(event);
        when(kafkaTemplate.send("hotel_availability_searches", "uuid-123", event))
                .thenReturn(CompletableFuture.completedFuture(null));

        searchKafkaProducer.publish(search);

        assertAll(
                () -> verify(searchEventMapper).toEvent(search),
                () -> verify(kafkaTemplate).send("hotel_availability_searches", "uuid-123", event)
        );
    }

    @Test
    void shouldHandleErrorWhenPublishingEventToKafka() {
        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        SearchCreatedEvent event = new SearchCreatedEvent(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(searchEventMapper.toEvent(search)).thenReturn(event);

        CompletableFuture<SendResult<String, SearchCreatedEvent>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka failure"));

        when(kafkaTemplate.send("hotel_availability_searches", "uuid-123", event))
                .thenReturn(failedFuture);

        searchKafkaProducer.publish(search);

        assertAll(
                () -> verify(searchEventMapper).toEvent(search),
                () -> verify(kafkaTemplate).send("hotel_availability_searches", "uuid-123", event)
        );
    }
}