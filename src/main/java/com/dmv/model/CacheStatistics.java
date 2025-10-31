package com.dmv.model;

import com.dmv.cache.CacheStats;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Overall cache statistics for the external data service
 */
public class CacheStatistics {
    
    private final long totalHitCount;
    private final long totalMissCount;
    private final double overallHitRatio;
    private final long totalEvictionCount;
    private final long totalSize;
    private final long totalMaxSize;
    private final List<CacheStats> layerStats;
    private final LocalDateTime timestamp;

    public CacheStatistics(long totalHitCount, long totalMissCount, double overallHitRatio,
                          long totalEvictionCount, long totalSize, long totalMaxSize,
                          List<CacheStats> layerStats) {
        this.totalHitCount = totalHitCount;
        this.totalMissCount = totalMissCount;
        this.overallHitRatio = overallHitRatio;
        this.totalEvictionCount = totalEvictionCount;
        this.totalSize = totalSize;
        this.totalMaxSize = totalMaxSize;
        this.layerStats = layerStats;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public long getTotalHitCount() {
        return totalHitCount;
    }

    public long getTotalMissCount() {
        return totalMissCount;
    }

    public double getOverallHitRatio() {
        return overallHitRatio;
    }

    public long getTotalEvictionCount() {
        return totalEvictionCount;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getTotalMaxSize() {
        return totalMaxSize;
    }

    public List<CacheStats> getLayerStats() {
        return layerStats;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "CacheStatistics{" +
                "totalHitCount=" + totalHitCount +
                ", totalMissCount=" + totalMissCount +
                ", overallHitRatio=" + String.format("%.2f%%", overallHitRatio * 100) +
                ", totalEvictionCount=" + totalEvictionCount +
                ", totalSize=" + totalSize +
                ", totalMaxSize=" + totalMaxSize +
                ", layerCount=" + layerStats.size() +
                ", timestamp=" + timestamp +
                '}';
    }
}