package com.dmv.cache;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Detailed cache metrics tracking for performance analysis
 */
public class CacheMetrics {
    
    private final String cacheName;
    private final String cacheType;
    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    private final AtomicLong evictionCount = new AtomicLong(0);
    private final AtomicLong totalGetTime = new AtomicLong(0);
    private final AtomicLong totalPutTime = new AtomicLong(0);
    private final AtomicLong getOperationCount = new AtomicLong(0);
    private final AtomicLong putOperationCount = new AtomicLong(0);
    private final AtomicReference<LocalDateTime> lastAccessed = new AtomicReference<>(LocalDateTime.now());
    private final LocalDateTime createdAt = LocalDateTime.now();
    
    public CacheMetrics(String cacheName, String cacheType) {
        this.cacheName = cacheName;
        this.cacheType = cacheType;
    }
    
    public void recordHit(long operationTimeMs) {
        hitCount.incrementAndGet();
        totalGetTime.addAndGet(operationTimeMs);
        getOperationCount.incrementAndGet();
        lastAccessed.set(LocalDateTime.now());
    }
    
    public void recordMiss(long operationTimeMs) {
        missCount.incrementAndGet();
        totalGetTime.addAndGet(operationTimeMs);
        getOperationCount.incrementAndGet();
        lastAccessed.set(LocalDateTime.now());
    }
    
    public void recordPut(long operationTimeMs) {
        totalPutTime.addAndGet(operationTimeMs);
        putOperationCount.incrementAndGet();
        lastAccessed.set(LocalDateTime.now());
    }
    
    public void recordEviction() {
        evictionCount.incrementAndGet();
    }
    
    public void reset() {
        hitCount.set(0);
        missCount.set(0);
        evictionCount.set(0);
        totalGetTime.set(0);
        totalPutTime.set(0);
        getOperationCount.set(0);
        putOperationCount.set(0);
        lastAccessed.set(LocalDateTime.now());
    }
    
    public CacheStats getStats(long currentSize, long maxSize) {
        long hits = hitCount.get();
        long misses = missCount.get();
        long evictions = evictionCount.get();
        long avgGetTime = getOperationCount.get() > 0 ? 
            totalGetTime.get() / getOperationCount.get() : 0;
        long avgPutTime = putOperationCount.get() > 0 ? 
            totalPutTime.get() / putOperationCount.get() : 0;
        
        return new CacheStats(
            cacheName, hits, misses, evictions, currentSize, maxSize,
            avgGetTime, avgPutTime, lastAccessed.get(), createdAt, cacheType
        );
    }
    
    // Getters for individual metrics
    public long getHitCount() { return hitCount.get(); }
    public long getMissCount() { return missCount.get(); }
    public long getEvictionCount() { return evictionCount.get(); }
    public String getCacheName() { return cacheName; }
    public String getCacheType() { return cacheType; }
    public LocalDateTime getLastAccessed() { return lastAccessed.get(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
}