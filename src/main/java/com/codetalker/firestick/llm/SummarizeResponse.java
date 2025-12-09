package com.codetalker.firestick.llm;

public class SummarizeResponse {
    private String summary;
    private double confidence;
    private int tokensUsed;
    
    public SummarizeResponse() {
    }
    
    public SummarizeResponse(String summary, double confidence, int tokensUsed) {
        this.summary = summary;
        this.confidence = confidence;
        this.tokensUsed = tokensUsed;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public int getTokensUsed() {
        return tokensUsed;
    }
    
    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
}
