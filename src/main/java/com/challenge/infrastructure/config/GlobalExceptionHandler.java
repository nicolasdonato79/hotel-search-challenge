package com.challenge.infrastructure.config;

import com.challenge.domain.exception.InvalidSearchException;
import com.challenge.domain.exception.SearchNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSearchException.class)
    public ProblemDetail handleInvalidSearch(InvalidSearchException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid search");
        problemDetail.setDetail(ex.getMessage());
        enrichWithRequestDetails(problemDetail, request);
        return problemDetail;
    }

    @ExceptionHandler(SearchNotFoundException.class)
    public ProblemDetail handleSearchNotFound(SearchNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Search not found");
        problemDetail.setDetail(ex.getMessage());
        enrichWithRequestDetails(problemDetail, request);
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                addError(errors, error.getField(), error.getDefaultMessage())
        );

        ex.getBindingResult().getGlobalErrors().forEach(error ->
                addError(errors, "_global", error.getDefaultMessage())
        );

        String detail = errors.isEmpty()
                ? "Request validation failed"
                : errors.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(message -> entry.getKey() + ": " + message))
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation error");
        problemDetail.setDetail(detail);
        problemDetail.setProperty("errors", errors);
        enrichWithRequestDetails(problemDetail, request);

        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        ex.getConstraintViolations().forEach(violation ->
                addError(errors, extractLastNode(violation.getPropertyPath()), violation.getMessage())
        );

        String detail = errors.isEmpty()
                ? "Constraint validation failed"
                : errors.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(message -> entry.getKey() + ": " + message))
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation error");
        problemDetail.setDetail(detail);
        problemDetail.setProperty("errors", errors);
        enrichWithRequestDetails(problemDetail, request);

        return problemDetail;
    }

    private void addError(Map<String, List<String>> errors, String key, String message) {
        String safeMessage = (message == null || message.isBlank()) ? "Invalid value" : message;
        errors.computeIfAbsent(key, ignored -> new ArrayList<>()).add(safeMessage);
    }

    private String extractLastNode(Path propertyPath) {
        String lastNode = null;
        for (Path.Node node : propertyPath) {
            lastNode = node.getName();
        }
        return lastNode != null ? lastNode : propertyPath.toString();
    }

    private void enrichWithRequestDetails(ProblemDetail problemDetail, HttpServletRequest request) {
        if (request != null) {
            problemDetail.setProperty("timestamp", Instant.now().toString());
            problemDetail.setProperty("path", request.getRequestURI());
            problemDetail.setProperty("method", request.getMethod());
        }
    }
}