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
    private long hitCount = 0;
    private long missCount = 0;
    
    // Network simulation parameters
    private final long baseLatencyMs = 50; // Base network latency
    private final long varianceMs = 30;    // Latency variance
    private final boolean simulationEnabled = true;

    public NetworkCacheLayer(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
        if (this.cache == null) {
            throw new IllegalStateException("Network cache '" + CACHE_NAME + "' not found in cache manager");
        }
        logger.info("Network cache layer initialized: {} (simulation: {})", CACHE_NAME, simulationEnabled);
    }

    @Override
    public Optional<Object> get(String key) {
        simulateNetworkLatency();
        
        try {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                hitCount++;
                logger.debug("Network cache HIT for key: {} (latency: {}ms)", key, getLastLatency());
                return Optional.of(wrapper.get());
            } else {
                missCount++;
                logger.debug("Network cache MISS for key: {} (latency: {}ms)", key, getLastLatency());
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error retrieving from network cache for key: {}", key, e);
            missCount++;
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, Object value) {
        simulateNetworkLatency();
        
        try {
            cache.put(key, value);
            logger.debug("Stored in network cache - key: {}, value type: {} (latency: {}ms)", 
                        key, value != null ? value.getClass().getSimpleName() : "null", getLastLatency());
        } catch (Exception e) {
            logger.error("Error storing in network cache for key: {}", key, e);
        }
    }

    @Override
    public void invalidate(String key) {
        simulateNetworkLatency();
        
        try {
            cache.evict(key);
            logger.debug("Evicted from network cache - key: {} (latency: {}ms)", key, getLastLatency());
        } catch (Exception e) {
            logger.error("Error evicting from network cache for key: {}", key, e);
        }
    }

    @Override
    public void invalidateAll() {
        simulateNetworkLatency();
        
        try {
            cache.clear();
            hitCount = 0;
            missCount = 0;
            logger.info("Cleared all entries from network cache (latency: {}ms)", getLastLatency());
        } catch (Exception e) {
            logger.error("Error clearing network cache", e);
        }
    }

    @Override
    public CacheStats getStats() {
        return new CacheStats(
            CACHE_NAME,
            hitCount,
            missCount,
            0, // eviction count not tracked in this simple implementation
            0, // current size not easily available without iterating
            5000 // max size from configuration
        );
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