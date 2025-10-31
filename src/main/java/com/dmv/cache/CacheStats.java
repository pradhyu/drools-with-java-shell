package com.dmv.cache;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Enhanced cache statistics data class with detailed metrics
 */
public class CacheStats {
    
    private final String cacheName;
    private final long hitCount;
    private final long missCount;
    private final long evictionCount;
    private final long size;
    private final long maxSize;
    private final double hitRatio;
    private final long averageGetTime; // in milliseconds
    private final long averagePutTime; // in milliseconds
    private final LocalDateTime lastAccessed;
    private final LocalDateTime createdAt;
    private final String cacheType; // "MEMORY", "NETWORK", "STORAGE"

    public CacheStats(String cacheName, long hitCount, long missCount, 
                     long evictionCount, long size, long maxSize) {
        this(cacheName, hitCount, missCount, evictionCount, size, maxSize, 
             0L, 0L, LocalDateTime.now(), LocalDateTime.now(), "UNKNOWN");
    }

    public CacheStats(String cacheName, long hitCount, long missCount, 
                     long evictionCount, long size, long maxSize,
                     long averageGetTime, long averagePutTime,
                     LocalDateTime lastAccessed, LocalDateTime createdAt,
                     String cacheType) {
        this.cacheName = cacheName;
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.evictionCount = evictionCount;
        this.size = size;
        this.maxSize = maxSize;
        this.hitRatio = (hitCount + missCount) > 0 ? 
            (double) hitCount / (hitCount + missCount) : 0.0;
        this.averageGetTime = averageGetTime;
        this.averagePutTime = averagePutTime;
        this.lastAccessed = lastAccessed;
        this.createdAt = createdAt;
        this.cacheType = cacheType;
    }

    // Getters
    public String getCacheName() {
        return cacheName;
    }

    public long getHitCount() {
        return hitCount;
    }

    public long getMissCount() {
        return missCount;
    }

    public long getEvictionCount() {
        return evictionCount;
    }

    public long getSize() {
        return size;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public double getHitRatio() {
        return hitRatio;
    }

    public long getAverageGetTime() {
        return averageGetTime;
    }

    public long getAveragePutTime() {
        return averagePutTime;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCacheType() {
        return cacheType;
    }

    public long getTotalRequests() {
        return hitCount + missCount;
    }

    @Override
    public String toString() {
        return "CacheStats{" +
                "cacheName='" + cacheName + '\'' +
                ", cacheType='" + cacheType + '\'' +
                ", hitCount=" + hitCount +
                ", missCount=" + missCount +
                ", hitRatio=" + String.format("%.2f%%", hitRatio * 100) +
                ", size=" + size +
                ", maxSize=" + maxSize +
                ", evictionCount=" + evictionCount +
                ", avgGetTime=" + averageGetTime + "ms" +
                ", avgPutTime=" + averagePutTime + "ms" +
                ", lastAccessed=" + lastAccessed +
                '}';
    }
}
}