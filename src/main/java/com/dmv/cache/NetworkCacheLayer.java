package com.dmv.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;

/**
 * Network cache layer implementation with simulated network latency
 * This is the second cache layer with larger capacity but slower access
 */
@Component
public class NetworkCacheLayer implements CacheLayer<String, Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(NetworkCacheLayer.class);
    private static final String CACHE_NAME = "networkCache";
    
    private final Cache cache;
    private final Random random = new Random();
    private final CacheMetrics metrics;
    
    // Network simulation parameters
    private final long baseLatencyMs = 50; // Base network latency
    private final long varianceMs = 30;    // Latency variance
    private final boolean simulationEnabled = true;
    private static final String CACHE_TYPE = "NETWORK";

    public NetworkCacheLayer(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
        if (this.cache == null) {
            throw new IllegalStateException("Network cache '" + CACHE_NAME + "' not found in cache manager");
        }
        this.metrics = new CacheMetrics(CACHE_NAME, CACHE_TYPE);
        logger.info("Network cache layer initialized: {} (simulation: {}) with enhanced metrics", CACHE_NAME, simulationEnabled);
    }

    @Override
    public Optional<Object> get(String key) {
        long startTime = System.currentTimeMillis();
        simulateNetworkLatency();
        
        try {
            Cache.ValueWrapper wrapper = cache.get(key);
            long operationTime = System.currentTimeMillis() - startTime;
            
            if (wrapper != null) {
                metrics.recordHit(operationTime);
                logger.debug("Network cache HIT for key: {} ({}ms) [CACHE_LAYER: NETWORK]", key, operationTime);
                return Optional.of(wrapper.get());
            } else {
                metrics.recordMiss(operationTime);
                logger.debug("Network cache MISS for key: {} ({}ms) [CACHE_LAYER: NETWORK]", key, operationTime);
                return Optional.empty();
            }
        } catch (Exception e) {
            long operationTime = System.currentTimeMillis() - startTime;
            metrics.recordMiss(operationTime);
            logger.error("Error retrieving from network cache for key: {} ({}ms) [CACHE_LAYER: NETWORK]", key, operationTime, e);
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, Object value) {
        long startTime = System.currentTimeMillis();
        simulateNetworkLatency();
        
        try {
            cache.put(key, value);
            long operationTime = System.currentTimeMillis() - startTime;
            metrics.recordPut(operationTime);
            logger.debug("Stored in network cache - key: {}, value type: {} ({}ms) [CACHE_LAYER: NETWORK]", 
                        key, value != null ? value.getClass().getSimpleName() : "null", operationTime);
        } catch (Exception e) {
            long operationTime = System.currentTimeMillis() - startTime;
            logger.error("Error storing in network cache for key: {} ({}ms) [CACHE_LAYER: NETWORK]", key, operationTime, e);
        }
    }

    @Override
    public void invalidate(String key) {
        simulateNetworkLatency();
        
        try {
            cache.evict(key);
            metrics.recordEviction();
            logger.debug("Evicted from network cache - key: {} [CACHE_LAYER: NETWORK]", key);
        } catch (Exception e) {
            logger.error("Error evicting from network cache for key: {} [CACHE_LAYER: NETWORK]", key, e);
        }
    }

    @Override
    public void invalidateAll() {
        simulateNetworkLatency();
        
        try {
            cache.clear();
            metrics.reset();
            logger.info("Cleared all entries from network cache [CACHE_LAYER: NETWORK]");
        } catch (Exception e) {
            logger.error("Error clearing network cache [CACHE_LAYER: NETWORK]", e);
        }
    }

    @Override
    public CacheStats getStats() {
        return metrics.getStats(0, 5000); // size not easily available, max size from config
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

    private long lastLatency = 0;

    private void simulateNetworkLatency() {
        if (!simulationEnabled) {
            return;
        }
        
        try {
            // Calculate random latency within variance
            lastLatency = baseLatencyMs + random.nextInt((int) varianceMs);
            Thread.sleep(lastLatency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Network latency simulation interrupted", e);
        }
    }

    private long getLastLatency() {
        return lastLatency;
    }

    /**
     * Enable or disable network simulation
     * @param enabled true to enable simulation, false to disable
     */
    public void setSimulationEnabled(boolean enabled) {
        logger.info("Network cache simulation {}", enabled ? "enabled" : "disabled");
    }
}