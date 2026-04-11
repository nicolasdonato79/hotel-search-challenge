package com.challenge.infrastructure.adapter.in.rest;

import com.challenge.domain.port.in.CreateSearchUseCase;
import com.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDto;
import com.challenge.infrastructure.adapter.in.rest.dto.SearchResponseDto;
import com.challenge.infrastructure.adapter.in.rest.mapper.SearchRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {

    private final CreateSearchUseCase createSearchUseCase;
    private final SearchRestMapper searchRestMapper;

    public SearchController(CreateSearchUseCase createSearchUseCase,
                            SearchRestMapper searchRestMapper) {
        this.createSearchUseCase = createSearchUseCase;
        this.searchRestMapper = searchRestMapper;
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a hotel search")
    public SearchResponseDto create(@Valid @RequestBody SearchRequestDto request) {
        String searchId = createSearchUseCase.create(searchRestMapper.toDomain(request));
        return new SearchResponseDto(searchId);
    }

    @GetMapping("/count")
    public String count(@RequestParam String searchId) {
        return "TODO";
    }
}
