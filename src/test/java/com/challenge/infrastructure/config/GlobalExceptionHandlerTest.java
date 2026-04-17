package com.challenge.infrastructure.config;

import com.challenge.domain.exception.InvalidSearchException;
import com.challenge.domain.exception.SearchNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final HttpServletRequest mockRequest = createMockRequest();

    private static HttpServletRequest createMockRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/search");
        when(request.getMethod()).thenReturn("POST");
        return request;
    }

    @Test
    void shouldHandleInvalidSearchException() {
        ProblemDetail result = handler.handleInvalidSearch(new InvalidSearchException("checkIn must be before checkOut"), mockRequest);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus()),
                () -> assertEquals("Invalid search", result.getTitle()),
                () -> assertEquals("checkIn must be before checkOut", result.getDetail()),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    @Test
    void shouldHandleSearchNotFoundException() {
        ProblemDetail result = handler.handleSearchNotFound(new SearchNotFoundException("missing-uuid"), mockRequest);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatus()),
                () -> assertEquals("Search not found", result.getTitle()),
                () -> assertEquals("Search not found for searchId: missing-uuid", result.getDetail()),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    @Test
    void shouldHandleMethodArgumentValidationWithMultipleMessagesPerField() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("searchRequest", "hotelId", "must not be blank"),
                new FieldError("searchRequest", "hotelId", "size must be between 1 and 7")
        ));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(
                new ObjectError("searchRequest", "invalid combination")
        ));

        ProblemDetail result = handler.handleValidation(ex, mockRequest);
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errors = (Map<String, List<String>>) result.getProperties().get("errors");

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus()),
                () -> assertEquals("Validation error", result.getTitle()),
                () -> assertTrue(result.getDetail().contains("hotelId: must not be blank")),
                () -> assertTrue(result.getDetail().contains("hotelId: size must be between 1 and 7")),
                () -> assertTrue(result.getDetail().contains("_global: invalid combination")),
                () -> assertEquals(List.of("must not be blank", "size must be between 1 and 7"), errors.get("hotelId")),
                () -> assertEquals(List.of("invalid combination"), errors.get("_global")),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    @Test
    void shouldUseFallbackMessageForBlankOrNullValidationMessages() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("searchRequest", "ages", null)
        ));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(
                new ObjectError("searchRequest", "   ")
        ));

        ProblemDetail result = handler.handleValidation(ex, mockRequest);
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errors = (Map<String, List<String>>) result.getProperties().get("errors");

        assertAll(
                () -> assertEquals(List.of("Invalid value"), errors.get("ages")),
                () -> assertEquals(List.of("Invalid value"), errors.get("_global")),
                () -> assertTrue(result.getDetail().contains("ages: Invalid value")),
                () -> assertTrue(result.getDetail().contains("_global: Invalid value")),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    @Test
    void shouldReturnGenericDetailWhenValidationErrorsAreEmpty() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
        when(bindingResult.getGlobalErrors()).thenReturn(Collections.emptyList());

        ProblemDetail result = handler.handleValidation(ex, mockRequest);
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errors = (Map<String, List<String>>) result.getProperties().get("errors");

        assertAll(
                () -> assertEquals("Request validation failed", result.getDetail()),
                () -> assertTrue(errors.isEmpty()),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    @Test
    void shouldHandleConstraintViolationsUsingLastNode() {
        ConstraintViolation<?> firstViolation = mock(ConstraintViolation.class);
        ConstraintViolation<?> secondViolation = mock(ConstraintViolation.class);
        Path firstPath = pathWithNodes("request", "searchId");
        Path secondPath = pathWithNodes("request", "searchId");

        when(firstViolation.getPropertyPath()).thenReturn(firstPath);
        when(firstViolation.getMessage()).thenReturn("must not be blank");
        when(secondViolation.getPropertyPath()).thenReturn(secondPath);
        when(secondViolation.getMessage()).thenReturn("size must be between 1 and 50");

        Set<ConstraintViolation<?>> violations = new LinkedHashSet<>(List.of(firstViolation, secondViolation));
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ProblemDetail result = handler.handleConstraintViolation(ex, mockRequest);
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errors = (Map<String, List<String>>) result.getProperties().get("errors");

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus()),
                () -> assertEquals("Validation error", result.getTitle()),
                () -> assertEquals(2, errors.get("searchId").size()),
                () -> assertTrue(errors.get("searchId").contains("must not be blank")),
                () -> assertTrue(errors.get("searchId").contains("size must be between 1 and 50")),
                () -> assertTrue(result.getDetail().contains("searchId: must not be blank")),
                () -> assertTrue(result.getDetail().contains("searchId: size must be between 1 and 50")),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    @Test
    void shouldUsePathToStringAndFallbackMessageWhenPathNodesAreMissing() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = pathWithoutNodes("searchId");

        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(" ");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ProblemDetail result = handler.handleConstraintViolation(ex, mockRequest);
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errors = (Map<String, List<String>>) result.getProperties().get("errors");

        assertAll(
                () -> assertEquals(List.of("Invalid value"), errors.get("searchId")),
                () -> assertTrue(result.getDetail().contains("searchId: Invalid value")),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    @Test
    void shouldReturnGenericDetailWhenConstraintViolationsAreEmpty() {
        ConstraintViolationException ex = new ConstraintViolationException(Set.of());

        ProblemDetail result = handler.handleConstraintViolation(ex, mockRequest);
        @SuppressWarnings("unchecked")
        Map<String, List<String>> errors = (Map<String, List<String>>) result.getProperties().get("errors");

        assertAll(
                () -> assertEquals("Constraint validation failed", result.getDetail()),
                () -> assertTrue(errors.isEmpty()),
                () -> assertNotNull(result.getProperties().get("timestamp")),
                () -> assertEquals("/search", result.getProperties().get("path")),
                () -> assertEquals("POST", result.getProperties().get("method"))
        );
    }

    private Path pathWithNodes(String... names) {
        Path path = mock(Path.class);
        List<Path.Node> nodes = java.util.Arrays.stream(names)
                .map(this::pathNode)
                .toList();
        when(path.iterator()).thenReturn(nodes.iterator());
        when(path.toString()).thenReturn(String.join(".", names));
        return path;
    }

    private Path pathWithoutNodes(String pathValue) {
        Path path = mock(Path.class);
        when(path.iterator()).thenReturn(Collections.emptyIterator());
        when(path.toString()).thenReturn(pathValue);
        return path;
    }

    private Path.Node pathNode(String name) {
        Path.Node node = mock(Path.Node.class);
        when(node.getName()).thenReturn(name);
        return node;
    }
}
