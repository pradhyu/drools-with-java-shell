package com.dmv.cache;

import java.util.Optional;

/**
 * Generic interface for cache layer implementations
 * @param <K> Key type
 * @param <V> Value type
 */
public interface CacheLayer<K, V> {
    
    /**
     * Retrieve value from cache
     * @param key Cache key
     * @return Optional containing value if found, empty otherwise
     */
    Optional<V> get(K key);
    
    /**
     * Store value in cache
     * @param key Cache key
     * @param value Value to store
     */
    void put(K key, V value);
    
    /**
     * Remove specific key from cache
     * @param key Cache key to remove
     */
    void invalidate(K key);
    
    /**
     * Clear all entries from cache
     */
    void invalidateAll();
    
    /**
     * Get cache statistics
     * @return Cache statistics object
     */
    CacheStats getStats();
    
    /**
     * Get cache name/identifier
     * @return Cache name
     */
    String getName();
}