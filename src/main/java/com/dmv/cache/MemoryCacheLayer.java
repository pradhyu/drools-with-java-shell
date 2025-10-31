package com.dmv.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Memory cache layer implementation using EhCache
 * This is the fastest cache layer with limited capacity
 */
@Component
public class MemoryCacheLayer implements CacheLayer<String, Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(MemoryCacheLayer.class);
    private static final String CACHE_NAME = "memoryCache";
    private static final String CACHE_TYPE = "MEMORY";
    
    private final Cache cache;
    private final CacheMetrics metrics;

    public MemoryCacheLayer(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
        if (this.cache == null) {
            throw new IllegalStateException("Memory cache '" + CACHE_NAME + "' not found in cache manager");
        }
        this.metrics = new CacheMetrics(CACHE_NAME, CACHE_TYPE);
        logger.info("Memory cache layer initialized: {} with enhanced metrics", CACHE_NAME);
    }

    @Override
    public Optional<Object> get(String key) {
        long startTime = System.currentTimeMillis();
        try {
            Cache.ValueWrapper wrapper = cache.get(key);
            long operationTime = System.currentTimeMillis() - startTime;
            
            if (wrapper != null) {
                metrics.recordHit(operationTime);
                logger.debug("Memory cache HIT for key: {} ({}ms) [CACHE_LAYER: MEMORY]", key, operationTime);
                return Optional.of(wrapper.get());
            } else {
                metrics.recordMiss(operationTime);
                logger.debug("Memory cache MISS for key: {} ({}ms) [CACHE_LAYER: MEMORY]", key, operationTime);
                return Optional.empty();
            }
        } catch (Exception e) {
            long operationTime = System.currentTimeMillis() - startTime;
            metrics.recordMiss(operationTime);
            logger.error("Error retrieving from memory cache for key: {} ({}ms) [CACHE_LAYER: MEMORY]", key, operationTime, e);
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, Object value) {
        long startTime = System.currentTimeMillis();
        try {
            cache.put(key, value);
            long operationTime = System.currentTimeMillis() - startTime;
            metrics.recordPut(operationTime);
            logger.debug("Stored in memory cache - key: {}, value type: {} ({}ms) [CACHE_LAYER: MEMORY]", 
                        key, value != null ? value.getClass().getSimpleName() : "null", operationTime);
        } catch (Exception e) {
            long operationTime = System.currentTimeMillis() - startTime;
            logger.error("Error storing in memory cache for key: {} ({}ms) [CACHE_LAYER: MEMORY]", key, operationTime, e);
        }
    }

    @Override
    public void invalidate(String key) {
        try {
            cache.evict(key);
            metrics.recordEviction();
            logger.debug("Evicted from memory cache - key: {} [CACHE_LAYER: MEMORY]", key);
        } catch (Exception e) {
            logger.error("Error evicting from memory cache for key: {} [CACHE_LAYER: MEMORY]", key, e);
        }
    }

    @Override
    public void invalidateAll() {
        try {
            cache.clear();
            metrics.reset();
            logger.info("Cleared all entries from memory cache [CACHE_LAYER: MEMORY]");
        } catch (Exception e) {
            logger.error("Error clearing memory cache [CACHE_LAYER: MEMORY]", e);
        }
    }

    @Override
    public CacheStats getStats() {
        return metrics.getStats(0, 1000); // size not easily available, max size from config
    }
    
    /**
     * Get detailed cache metrics for testing and monitoring
     */
    public CacheMetrics getMetrics() {
        return metrics;
    }

    @Override
    public String getName() {
        return CACHE_NAME;
    }
}