package com.dmv.model;

import java.time.LocalDateTime;

/**
 * Represents a cache layer access event for detailed tracking
 */
public class CacheLayerAccess {
    
    private final String cacheLayer; // "MEMORY", "NETWORK", "STORAGE"
    private final String operation; // "HIT", "MISS", "PUT", "EVICT"
    private final String key;
    private final long operationTimeMs;
    private final LocalDateTime timestamp;
    private final boolean successful;
    
    public CacheLayerAccess(String cacheLayer, String operation, String key, 
                           long operationTimeMs, boolean successful) {
        this.cacheLayer = cacheLayer;
        this.operation = operation;
        this.key = key;
        this.operationTimeMs = operationTimeMs;
        this.successful = successful;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public String getCacheLayer() { return cacheLayer; }
    public String getOperation() { return operation; }
    public String getKey() { return key; }
    public long getOperationTimeMs() { return operationTimeMs; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isSuccessful() { return successful; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s %s for key '%s' (%dms) - %s", 
            timestamp, cacheLayer, operation, key, operationTimeMs, 
            successful ? "SUCCESS" : "FAILED");
    }
}