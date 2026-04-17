package com.challenge.infrastructure.adapter.in.rest;

import com.challenge.application.port.in.CreateSearchUseCase;
import com.challenge.application.port.in.GetSearchCountUseCase;
import com.challenge.infrastructure.adapter.in.rest.dto.CountResponseDto;
import com.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDto;
import com.challenge.infrastructure.adapter.in.rest.dto.SearchResponseDto;
import com.challenge.infrastructure.adapter.in.rest.mapper.SearchCountRestMapper;
import com.challenge.infrastructure.adapter.in.rest.mapper.SearchRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {


    private final CreateSearchUseCase createSearchUseCase;
    private final GetSearchCountUseCase getSearchCountUseCase;
    private final SearchRestMapper searchRestMapper;
    private final SearchCountRestMapper searchCountRestMapper;

    public SearchController(CreateSearchUseCase createSearchUseCase,
                            GetSearchCountUseCase getSearchCountUseCase,
                            SearchRestMapper searchRestMapper,
                            SearchCountRestMapper searchCountRestMapper) {
        this.createSearchUseCase = createSearchUseCase;
        this.getSearchCountUseCase = getSearchCountUseCase;
        this.searchRestMapper = searchRestMapper;
        this.searchCountRestMapper = searchCountRestMapper;
    }


    @PostMapping("/search")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a hotel search")
    public SearchResponseDto create(@Valid @RequestBody SearchRequestDto request) {
        String searchId = createSearchUseCase.create(searchRestMapper.toDomain(request));
        return new SearchResponseDto(searchId);
    }

    @GetMapping("/count")
    @Operation(summary = "Get count of equal searches by searchId")
    public CountResponseDto count(@RequestParam @NotBlank String searchId) {
        return searchCountRestMapper.toDto(
                getSearchCountUseCase.getBySearchId(searchId)
        );
    }
}
