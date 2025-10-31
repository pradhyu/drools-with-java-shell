package com.dmv.model;

import com.dmv.cache.CacheStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    private final Map<String, Long> hitsByLayer;
    private final Map<String, Long> missesByLayer;
    private final Map<String, Double> avgResponseTimeByLayer;

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
        
        // Build layer-specific maps from layerStats
        this.hitsByLayer = new HashMap<>();
        this.missesByLayer = new HashMap<>();
        this.avgResponseTimeByLayer = new HashMap<>();
        
        for (CacheStats stats : layerStats) {
            hitsByLayer.put(stats.getCacheType(), stats.getHitCount());
            missesByLayer.put(stats.getCacheType(), stats.getMissCount());
            avgResponseTimeByLayer.put(stats.getCacheType(), (double) stats.getAverageGetTime());
        }
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

    public Map<String, Long> getHitsByLayer() {
        return hitsByLayer;
    }

    public Map<String, Long> getMissesByLayer() {
        return missesByLayer;
    }

    public Map<String, Double> getAvgResponseTimeByLayer() {
        return avgResponseTimeByLayer;
    }
    
    /**
     * Get hits for a specific cache layer
     */
    public long getHitsForLayer(String layerType) {
        return hitsByLayer.getOrDefault(layerType, 0L);
    }
    
    /**
     * Get misses for a specific cache layer
     */
    public long getMissesForLayer(String layerType) {
        return missesByLayer.getOrDefault(layerType, 0L);
    }
    
    /**
     * Get average response time for a specific cache layer
     */
    public double getAvgResponseTimeForLayer(String layerType) {
        return avgResponseTimeByLayer.getOrDefault(layerType, 0.0);
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