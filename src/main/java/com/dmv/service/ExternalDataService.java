package com.dmv.service;

import com.dmv.model.CacheStatistics;

import java.util.List;
import java.util.Map;

/**
 * Service interface for accessing external data with multi-layer caching
 */
public interface ExternalDataService {
    
    /**
     * Find entries in a collection by key-value filter
     * @param collection Collection name (JSON filename without extension)
     * @param key Key to filter by (supports dot notation for nested properties)
     * @param value Value to match
     * @return List of matching entries
     */
    List<Map<String, Object>> findByCollectionAndKey(String collection, String key, Object value);
    
    /**
     * Find entries in a collection where the specified key exists
     * @param collection Collection name
     * @param key Key that must exist in the entry
     * @return List of entries containing the key
     */
    List<Map<String, Object>> findByCollectionAndKeyExists(String collection, String key);
    
    /**
     * Get all entries from a collection
     * @param collection Collection name
     * @return List of all entries in the collection
     */
    List<Map<String, Object>> findByCollection(String collection);
    
    /**
     * Invalidate cache for a specific collection
     * @param collection Collection name to invalidate
     */
    void invalidateCache(String collection);
    
    /**
     * Invalidate all caches
     */
    void invalidateAllCaches();
    
    /**
     * Get cache statistics for all cache layers
     * @return Cache statistics object
     */
    CacheStatistics getCacheStatistics();
    
    /**
     * Get available collections
     * @return Set of available collection names
     */
    java.util.Set<String> getAvailableCollections();
    
    /**
     * Warm up cache for a specific collection
     * @param collection Collection name to warm up
     */
    void warmUpCache(String collection);
}