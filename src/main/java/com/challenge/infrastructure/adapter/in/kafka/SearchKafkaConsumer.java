package com.challenge.infrastructure.adapter.in.kafka;

import com.challenge.domain.model.Search;
import com.challenge.domain.port.out.SearchCommandRepository;
import com.challenge.infrastructure.adapter.out.kafka.dto.SearchCreatedEvent;
import com.challenge.infrastructure.adapter.out.kafka.mapper.SearchEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class SearchKafkaConsumer {

    private final SearchCommandRepository searchCommandRepository;
    private final SearchEventMapper searchEventMapper;
    private final ExecutorService virtualThreadExecutor;
    private static final Logger log = LoggerFactory.getLogger(SearchKafkaConsumer.class);

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
        log.info("Kafka event received. searchId={}", event.searchId());

        virtualThreadExecutor.submit(() -> {
            try {
                log.info("Virtual thread started. searchId={}", event.searchId());

                Search search = searchEventMapper.toDomain(event);
                searchCommandRepository.save(search);

                log.info("Search persisted successfully. searchId={}", event.searchId());
            } catch (Exception ex) {
                log.error("Error persisting search. searchId={}", event.searchId(), ex);
            }
        });
    }

}