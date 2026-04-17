package com.challenge.infrastructure.config;


import com.challenge.application.service.CreateSearchService;
import com.challenge.application.service.GetSearchCountService;
import com.challenge.application.port.in.CreateSearchUseCase;
import com.challenge.application.port.in.GetSearchCountUseCase;
import com.challenge.domain.port.out.SearchEventPublisher;
import com.challenge.domain.port.out.SearchIdGenerator;
import com.challenge.domain.port.out.SearchQueryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateSearchUseCase createSearchUseCase(
            SearchEventPublisher searchEventPublisher,
            SearchIdGenerator searchIdGenerator
    ) {
        return new CreateSearchService(searchEventPublisher, searchIdGenerator);
    }

    @Bean
    public GetSearchCountUseCase getSearchCountUseCase(
            SearchQueryRepository searchQueryRepository
    ) {
        return new GetSearchCountService(searchQueryRepository);
    }
}