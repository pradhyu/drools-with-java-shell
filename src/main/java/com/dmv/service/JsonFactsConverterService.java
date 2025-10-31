package com.dmv.service;

import com.dmv.model.LicenseRenewalRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Service for converting JSON to fact objects and vice versa
 */
public interface JsonFactsConverterService {
    
    /**
     * Convert JSON string to LicenseRenewalRequest fact object
     * @param json JSON string representation
     * @return LicenseRenewalRequest object
     */
    LicenseRenewalRequest jsonToLicenseRenewalRequest(String json);
    
    /**
     * Convert fact object to JSON string
     * @param fact Fact object to convert
     * @return JSON string representation
     */
    String factToJson(Object fact);
    
    /**
     * Convert JSON array to list of fact objects
     * @param json JSON array string
     * @param factClass Class type of facts
     * @return List of fact objects
     */
    <T> List<T> jsonArrayToFacts(String json, Class<T> factClass);
    
    /**
     * Convert list of facts to JSON array
     * @param facts List of fact objects
     * @return JSON array string
     */
    String factsToJsonArray(List<Object> facts);
    
    /**
     * Get the ObjectMapper instance for direct use
     * @return Configured ObjectMapper
     */
    ObjectMapper getObjectMapper();
}