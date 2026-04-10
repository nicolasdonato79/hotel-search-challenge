package com.challenge.infrastructure.adapter.out.persistence.adapter;

import com.challenge.domain.model.Search;
import com.challenge.domain.model.SearchCriteria;
import com.challenge.domain.port.out.SearchCommandRepository;
import com.challenge.domain.port.out.SearchQueryRepository;
import com.challenge.infrastructure.adapter.out.persistence.entity.SearchEntity;
import com.challenge.infrastructure.adapter.out.persistence.mapper.SearchPersistenceMapper;
import com.challenge.infrastructure.adapter.out.persistence.repository.SpringDataSearchRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SearchPersistenceAdapter implements SearchCommandRepository, SearchQueryRepository {

    private final SpringDataSearchRepository repository;
    private final SearchPersistenceMapper mapper;

    public SearchPersistenceAdapter(SpringDataSearchRepository repository,
                                    SearchPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(Search search) {
        SearchEntity entity = mapper.toEntity(search);
        repository.save(entity);
    }

    @Override
    public Optional<Search> findBySearchId(String searchId) {
        return repository.findBySearchId(searchId)
                .map(mapper::toDomain);
    }

    @Override
    public long countByCriteria(SearchCriteria criteria) {
        return repository.countByHotelIdAndCheckInAndCheckOutAndAgesSerialized(
                criteria.hotelId(),
                criteria.checkIn(),
                criteria.checkOut(),
                mapper.serializeAges(criteria.ages())
        );
    }
}