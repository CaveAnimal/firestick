package com.codetalker.firestick.exception;

/**
 * Exception thrown when code parsing operations fail.
 */
public class CodeParsingException extends RuntimeException {
    private final String filePath;

    public CodeParsingException(String message, String filePath) {
        super(message);
        this.filePath = filePath;
    }

    public CodeParsingException(String message, String filePath, Throwable cause) {
        super(message, cause);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
