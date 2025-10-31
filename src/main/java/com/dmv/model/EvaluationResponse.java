package com.dmv.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class EvaluationResponse {
    
    private String applicantId;
    private RenewalDecision decision;
    private ExecutionMetadata executionMetadata;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime evaluatedAt;

    // Default constructor
    public EvaluationResponse() {
        this.evaluatedAt = LocalDateTime.now();
    }

    // Constructor
    public EvaluationResponse(String applicantId, RenewalDecision decision) {
        this.applicantId = applicantId;
        this.decision = decision;
        this.evaluatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public RenewalDecision getDecision() {
        return decision;
    }

    public void setDecision(RenewalDecision decision) {
        this.decision = decision;
    }

    public ExecutionMetadata getExecutionMetadata() {
        return executionMetadata;
    }

    public void setExecutionMetadata(ExecutionMetadata executionMetadata) {
        this.executionMetadata = executionMetadata;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    @Override
    public String toString() {
        return "EvaluationResponse{" +
                "applicantId='" + applicantId + '\'' +
                ", decision=" + decision +
                ", executionMetadata=" + executionMetadata +
                ", evaluatedAt=" + evaluatedAt +
                '}';
    }
}