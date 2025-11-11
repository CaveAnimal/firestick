package com.codetalker.firestick.exception;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileDiscoveryException.class)
    public ResponseEntity<ErrorResponse> handleFileDiscovery(FileDiscoveryException ex, HttpServletRequest request) {
        log.error("File discovery failed: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "File discovery failed", ex.getMessage(), request.getRequestURI(), "FILE_DISCOVERY_ERROR", null);
    }

    @ExceptionHandler(CodeParsingException.class)
    public ResponseEntity<ErrorResponse> handleCodeParsing(CodeParsingException ex, HttpServletRequest request) {
        String msg = ex.getMessage();
        if (ex.getFilePath() != null) {
            msg = msg + " [file=" + ex.getFilePath() + "]";
        }
        log.error("Code parsing failed: {}", msg, ex);
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Code parsing failed", msg, request.getRequestURI(), "CODE_PARSING_ERROR", null);
    }

    @ExceptionHandler(IndexingException.class)
    public ResponseEntity<ErrorResponse> handleIndexing(IndexingException ex, HttpServletRequest request) {
        log.error("Indexing failed: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Indexing failed", ex.getMessage(), request.getRequestURI(), "INDEXING_ERROR", null);
    }

    @ExceptionHandler(EmbeddingException.class)
    public ResponseEntity<ErrorResponse> handleEmbedding(EmbeddingException ex, HttpServletRequest request) {
        log.error("Embedding failed: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Embedding failed", ex.getMessage(), request.getRequestURI(), "EMBEDDING_ERROR", null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Validation error: missing parameter {}", ex.getParameterName());
        ErrorResponse.ErrorDetail detail = new ErrorResponse.ErrorDetail(ex.getParameterName(), "is required");
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI(), "VALIDATION_ERROR", List.of(detail));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Validation error: {} violations", ex.getConstraintViolations().size());
        List<ErrorResponse.ErrorDetail> details = ex.getConstraintViolations().stream()
            .map(this::toDetail)
            .toList();
        return build(HttpStatus.BAD_REQUEST, "Bad Request", "Validation failed", request.getRequestURI(), "VALIDATION_ERROR", details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors();
        log.warn("Validation error: {} violations (body)", fieldErrors.size());
    List<ErrorResponse.ErrorDetail> details = fieldErrors.stream()
        .map(fe -> new ErrorResponse.ErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Bad Request", "Validation failed", request.getRequestURI(), "VALIDATION_ERROR", details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex.getMessage(), request.getRequestURI(), "INTERNAL_ERROR", null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, String path, String code, List<ErrorResponse.ErrorDetail> details) {
        // Correlate with MDC request id if present
        String requestId = org.slf4j.MDC.get("requestId");
        ErrorResponse body = new ErrorResponse(OffsetDateTime.now(), status.value(), error, message, path, requestId, code, details);
        return ResponseEntity.status(status).body(body);
    }

    private ErrorResponse.ErrorDetail toDetail(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
        String msg = violation.getMessage();
        return new ErrorResponse.ErrorDetail(field, msg);
    }
}
