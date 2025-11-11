package com.codetalker.firestick.exception;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard error response payload for REST controllers.
 */
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    // Correlation id to trace across logs and client reports
    private String requestId;
    // Machine-readable code (e.g., VALIDATION_ERROR, INDEXING_ERROR)
    private String code;
    // Optional structured details (e.g., field errors)
    private List<ErrorDetail> details;

    public ErrorResponse() {}

    public ErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null, null, null);
    }

    public ErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path, String requestId, String code, List<ErrorDetail> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.requestId = requestId;
        this.code = code;
        this.details = details;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public List<ErrorDetail> getDetails() { return details; }
    public void setDetails(List<ErrorDetail> details) { this.details = details; }
    /** Simple name/message pair for detailed error reporting. */
    public static class ErrorDetail {
        private String field;
        private String message;

        public ErrorDetail() {}
        public ErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
