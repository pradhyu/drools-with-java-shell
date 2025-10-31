package com.dmv.service.impl;

import com.dmv.model.LicenseRenewalRequest;
import com.dmv.service.JsonFactsConverterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JsonFactsConverterServiceImpl implements JsonFactsConverterService {

    private static final Logger logger = LoggerFactory.getLogger(JsonFactsConverterServiceImpl.class);
    
    private final ObjectMapper objectMapper;

    public JsonFactsConverterServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        logger.info("JsonFactsConverter service initialized");
    }

    @Override
    public LicenseRenewalRequest jsonToLicenseRenewalRequest(String json) {
        logger.debug("Converting JSON to LicenseRenewalRequest");
        
        try {
            LicenseRenewalRequest request = objectMapper.readValue(json, LicenseRenewalRequest.class);
            logger.debug("Successfully converted JSON to LicenseRenewalRequest for applicant: {}", 
                        request.getApplicantId());
            return request;
            
        } catch (Exception e) {
            logger.error("Failed to convert JSON to LicenseRenewalRequest", e);
            throw new RuntimeException("JSON conversion failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String factToJson(Object fact) {
        logger.debug("Converting fact to JSON: {}", fact.getClass().getSimpleName());
        
        try {
            String json = objectMapper.writeValueAsString(fact);
            logger.debug("Successfully converted fact to JSON");
            return json;
            
        } catch (Exception e) {
            logger.error("Failed to convert fact to JSON", e);
            throw new RuntimeException("Fact to JSON conversion failed: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> List<T> jsonArrayToFacts(String json, Class<T> factClass) {
        logger.debug("Converting JSON array to facts of type: {}", factClass.getSimpleName());
        
        try {
            T[] array = (T[]) objectMapper.readValue(json, 
                java.lang.reflect.Array.newInstance(factClass, 0).getClass());
            List<T> facts = Arrays.asList(array);
            
            logger.debug("Successfully converted JSON array to {} facts", facts.size());
            return facts;
            
        } catch (Exception e) {
            logger.error("Failed to convert JSON array to facts", e);
            throw new RuntimeException("JSON array conversion failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String factsToJsonArray(List<Object> facts) {
        logger.debug("Converting {} facts to JSON array", facts.size());
        
        try {
            String json = objectMapper.writeValueAsString(facts);
            logger.debug("Successfully converted facts to JSON array");
            return json;
            
        } catch (Exception e) {
            logger.error("Failed to convert facts to JSON array", e);
            throw new RuntimeException("Facts to JSON array conversion failed: " + e.getMessage(), e);
        }
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}