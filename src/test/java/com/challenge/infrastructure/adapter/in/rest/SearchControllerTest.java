package com.challenge.infrastructure.adapter.in.rest;

import com.challenge.domain.exception.InvalidSearchException;
import com.challenge.domain.exception.SearchNotFoundException;
import com.challenge.domain.model.Search;
import com.challenge.domain.model.SearchCountResult;
import com.challenge.application.port.in.CreateSearchUseCase;
import com.challenge.application.port.in.GetSearchCountUseCase;
import com.challenge.infrastructure.adapter.in.rest.mapper.SearchCountRestMapper;
import com.challenge.infrastructure.adapter.in.rest.mapper.SearchRestMapper;
import com.challenge.infrastructure.config.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(SearchController.class)
@Import({SearchRestMapper.class, SearchCountRestMapper.class, GlobalExceptionHandler.class})
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateSearchUseCase createSearchUseCase;

    @MockitoBean
    private GetSearchCountUseCase getSearchCountUseCase;

    @Test
    void shouldCreateSearch() throws Exception {
        when(createSearchUseCase.create(any())).thenReturn("uuid-123");

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": "1234aBc",
                                  "checkIn": "29/12/2023",
                                  "checkOut": "31/12/2023",
                                  "ages": [30, 29, 1, 3]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchId").value("uuid-123"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestValidationFails() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": "",
                                  "checkIn": "29/12/2023",
                                  "checkOut": "31/12/2023",
                                  "ages": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation error"));
    }

    @Test
    void shouldReturnBadRequestWhenBusinessValidationFails() throws Exception {
        when(createSearchUseCase.create(any()))
                .thenThrow(new InvalidSearchException("checkIn must be before checkOut"));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": "1234aBc",
                                  "checkIn": "31/12/2023",
                                  "checkOut": "29/12/2023",
                                  "ages": [30, 29, 1, 3]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid search"))
                .andExpect(jsonPath("$.detail").value("checkIn must be before checkOut"));
    }

    @Test
    void shouldReturnCountBySearchId() throws Exception {
        Search search = new Search(
                "uuid-123",
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3)
        );

        SearchCountResult result = new SearchCountResult("uuid-123", search, 2L);

        when(getSearchCountUseCase.getBySearchId("uuid-123")).thenReturn(result);

        mockMvc.perform(get("/count")
                        .param("searchId", "uuid-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchId").value("uuid-123"))
                .andExpect(jsonPath("$.search.hotelId").value("1234aBc"))
                .andExpect(jsonPath("$.search.checkIn").value("29/12/2023"))
                .andExpect(jsonPath("$.search.checkOut").value("31/12/2023"))
                .andExpect(jsonPath("$.search.ages[0]").value(30))
                .andExpect(jsonPath("$.search.ages[1]").value(29))
                .andExpect(jsonPath("$.search.ages[2]").value(1))
                .andExpect(jsonPath("$.search.ages[3]").value(3))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void shouldReturnNotFoundWhenSearchIdDoesNotExist() throws Exception {
        when(getSearchCountUseCase.getBySearchId("missing-uuid"))
                .thenThrow(new SearchNotFoundException("missing-uuid"));

        mockMvc.perform(get("/count")
                        .param("searchId", "missing-uuid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Search not found"))
                .andExpect(jsonPath("$.detail").value("Search not found for searchId: missing-uuid"));
    }

    @Test
    void shouldReturnBadRequestWhenSearchIdIsBlank() throws Exception {
        mockMvc.perform(get("/count")
                        .param("searchId", " "))
                .andExpect(status().isBadRequest());
    }
}