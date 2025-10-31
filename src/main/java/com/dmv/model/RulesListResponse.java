package com.dmv.model;

import java.util.ArrayList;
import java.util.List;

public class RulesListResponse {
    
    private List<RuleMetadata> rules = new ArrayList<>();
    private int totalCount;
    private int page;
    private int pageSize;

    // Default constructor
    public RulesListResponse() {}

    // Constructor
    public RulesListResponse(List<RuleMetadata> rules) {
        this.rules = rules != null ? rules : new ArrayList<>();
        this.totalCount = this.rules.size();
    }

    // Getters and Setters
    public List<RuleMetadata> getRules() {
        return rules;
    }

    public void setRules(List<RuleMetadata> rules) {
        this.rules = rules != null ? rules : new ArrayList<>();
        this.totalCount = this.rules.size();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    // Helper methods
    public void addRule(RuleMetadata rule) {
        if (rules == null) {
            rules = new ArrayList<>();
        }
        rules.add(rule);
        totalCount = rules.size();
    }

    @Override
    public String toString() {
        return "RulesListResponse{" +
                "totalCount=" + totalCount +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", rulesCount=" + (rules != null ? rules.size() : 0) +
                '}';
    }
}