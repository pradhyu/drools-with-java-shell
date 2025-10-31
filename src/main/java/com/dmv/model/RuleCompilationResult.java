package com.dmv.model;

import java.util.ArrayList;
import java.util.List;

public class RuleCompilationResult {
    
    private boolean success;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private String compiledRuleName;

    public RuleCompilationResult() {}

    public RuleCompilationResult(boolean success) {
        this.success = success;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public String getCompiledRuleName() {
        return compiledRuleName;
    }

    public void setCompiledRuleName(String compiledRuleName) {
        this.compiledRuleName = compiledRuleName;
    }

    // Helper methods
    public void addError(String error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
        this.success = false;
    }

    public void addWarning(String warning) {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        warnings.add(warning);
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }

    @Override
    public String toString() {
        return "RuleCompilationResult{" +
                "success=" + success +
                ", errors=" + errors +
                ", warnings=" + warnings +
                ", compiledRuleName='" + compiledRuleName + '\'' +
                '}';
    }
}