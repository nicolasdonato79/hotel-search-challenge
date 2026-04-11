package com.challenge.infrastructure.adapter.in.kafka;

import com.challenge.domain.model.Search;
import com.challenge.domain.port.out.SearchCommandRepository;
import com.challenge.infrastructure.adapter.out.kafka.dto.SearchCreatedEvent;
import com.challenge.infrastructure.adapter.out.kafka.mapper.SearchEventMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class SearchKafkaConsumer {

    private final SearchCommandRepository searchCommandRepository;
    private final SearchEventMapper searchEventMapper;
    private final ExecutorService virtualThreadExecutor;

    public SearchKafkaConsumer(SearchCommandRepository searchCommandRepository,
                               SearchEventMapper searchEventMapper,
                               ExecutorService virtualThreadExecutor) {
        this.searchCommandRepository = searchCommandRepository;
        this.searchEventMapper = searchEventMapper;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @KafkaListener(
            topics = "hotel_availability_searches",
            groupId = "hotel-search-group"
    )
    public void consume(SearchCreatedEvent event) {
        virtualThreadExecutor.submit(() -> {
            Search search = searchEventMapper.toDomain(event);
            searchCommandRepository.save(search);
        });
    }
}