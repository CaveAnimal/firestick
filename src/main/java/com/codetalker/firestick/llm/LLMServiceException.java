package com.codetalker.firestick.llm;

public class LLMServiceException extends Exception {
    public LLMServiceException(String message) {
        super(message);
    }
    
    public LLMServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
