package com.dmv.model;

import java.util.ArrayList;
import java.util.List;

public class ExecutionResult {
    
    private boolean success;
    private String output;
    private String errorOutput;
    private List<String> diagnostics = new ArrayList<>();
    private String resultValue;
    private String resultType;
    private long executionTimeMs;

    public ExecutionResult() {}

    public ExecutionResult(boolean success) {
        this.success = success;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public void setErrorOutput(String errorOutput) {
        this.errorOutput = errorOutput;
    }

    public List<String> getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(List<String> diagnostics) {
        this.diagnostics = diagnostics != null ? diagnostics : new ArrayList<>();
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    // Helper methods
    public void addDiagnostic(String diagnostic) {
        if (diagnostics == null) {
            diagnostics = new ArrayList<>();
        }
        diagnostics.add(diagnostic);
    }

    public boolean hasErrors() {
        return errorOutput != null && !errorOutput.trim().isEmpty();
    }

    public boolean hasDiagnostics() {
        return diagnostics != null && !diagnostics.isEmpty();
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "success=" + success +
                ", output='" + output + '\'' +
                ", errorOutput='" + errorOutput + '\'' +
                ", resultValue='" + resultValue + '\'' +
                ", resultType='" + resultType + '\'' +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}