package com.challenge.infrastructure.adapter.out.kafka;

import com.challenge.domain.model.Search;
import com.challenge.domain.port.out.SearchEventPublisher;
import com.challenge.infrastructure.adapter.out.kafka.dto.SearchCreatedEvent;
import com.challenge.infrastructure.adapter.out.kafka.mapper.SearchEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SearchKafkaProducer implements SearchEventPublisher {

    private static final String TOPIC = "hotel_availability_searches";
    private static final Logger log = LoggerFactory.getLogger(SearchKafkaProducer.class);

    private final KafkaTemplate<String, SearchCreatedEvent> kafkaTemplate;
    private final SearchEventMapper searchEventMapper;

    public SearchKafkaProducer(KafkaTemplate<String, SearchCreatedEvent> kafkaTemplate,
                               SearchEventMapper searchEventMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.searchEventMapper = searchEventMapper;
    }

    @Override
    public void publish(Search search) {
        SearchCreatedEvent event = searchEventMapper.toEvent(search);

        kafkaTemplate.send(TOPIC, search.searchId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error publishing event. searchId={}", search.searchId(), ex);
                    } else {
                        log.info("Event published successfully. topic={}, searchId={}, partition={}, offset={}",
                                TOPIC,
                                search.searchId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}