package com.dmv.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Memory cache layer implementation using EhCache
 * This is the fastest cache layer with limited capacity
 */
@Component
public class MemoryCacheLayer implements CacheLayer<String, Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(MemoryCacheLayer.class);
    private static final String CACHE_NAME = "memoryCache";
    
    private final Cache cache;
    private long hitCount = 0;
    private long missCount = 0;

    public MemoryCacheLayer(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
        if (this.cache == null) {
            throw new IllegalStateException("Memory cache '" + CACHE_NAME + "' not found in cache manager");
        }
        logger.info("Memory cache layer initialized: {}", CACHE_NAME);
    }

    @Override
    public Optional<Object> get(String key) {
        try {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                hitCount++;
                logger.debug("Memory cache HIT for key: {}", key);
                return Optional.of(wrapper.get());
            } else {
                missCount++;
                logger.debug("Memory cache MISS for key: {}", key);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error retrieving from memory cache for key: {}", key, e);
            missCount++;
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, Object value) {
        try {
            cache.put(key, value);
            logger.debug("Stored in memory cache - key: {}, value type: {}", 
                        key, value != null ? value.getClass().getSimpleName() : "null");
        } catch (Exception e) {
            logger.error("Error storing in memory cache for key: {}", key, e);
        }
    }

    @Override
    public void invalidate(String key) {
        try {
            cache.evict(key);
            logger.debug("Evicted from memory cache - key: {}", key);
        } catch (Exception e) {
            logger.error("Error evicting from memory cache for key: {}", key, e);
        }
    }

    @Override
    public void invalidateAll() {
        try {
            cache.clear();
            hitCount = 0;
            missCount = 0;
            logger.info("Cleared all entries from memory cache");
        } catch (Exception e) {
            logger.error("Error clearing memory cache", e);
        }
    }

    @Override
    public CacheStats getStats() {
        // For EhCache, we'll use our internal counters
        // In a production environment, you might want to use EhCache's built-in statistics
        return new CacheStats(
            CACHE_NAME,
            hitCount,
            missCount,
            0, // eviction count not tracked in this simple implementation
            0, // current size not easily available without iterating
            1000 // max size from configuration
        );
    }

    @Override
    public String getName() {
        return CACHE_NAME;
    }
}