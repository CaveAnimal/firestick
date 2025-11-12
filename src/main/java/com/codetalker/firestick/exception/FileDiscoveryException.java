package com.codetalker.firestick.exception;

/**
 * Exception thrown when file discovery operations fail.
 */
public class FileDiscoveryException extends RuntimeException {
    public FileDiscoveryException(String message) {
        super(message);
    }

    public FileDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
