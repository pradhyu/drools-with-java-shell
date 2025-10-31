package com.dmv.cache;

/**
 * Cache statistics data class
 */
public class CacheStats {
    
    private final String cacheName;
    private final long hitCount;
    private final long missCount;
    private final long evictionCount;
    private final long size;
    private final long maxSize;
    private final double hitRatio;

    public CacheStats(String cacheName, long hitCount, long missCount, 
                     long evictionCount, long size, long maxSize) {
        this.cacheName = cacheName;
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.evictionCount = evictionCount;
        this.size = size;
        this.maxSize = maxSize;
        this.hitRatio = (hitCount + missCount) > 0 ? 
            (double) hitCount / (hitCount + missCount) : 0.0;
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

    @Override
    public String toString() {
        return "CacheStats{" +
                "cacheName='" + cacheName + '\'' +
                ", hitCount=" + hitCount +
                ", missCount=" + missCount +
                ", hitRatio=" + String.format("%.2f%%", hitRatio * 100) +
                ", size=" + size +
                ", maxSize=" + maxSize +
                ", evictionCount=" + evictionCount +
                '}';
    }
}