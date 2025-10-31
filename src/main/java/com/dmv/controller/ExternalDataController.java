package com.dmv.controller;

import com.dmv.model.CacheStatistics;
import com.dmv.service.ExternalDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for external data service operations
 */
@RestController
@RequestMapping("/api/external-data")
public class ExternalDataController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalDataController.class);
    
    private final ExternalDataService externalDataService;

    @Autowired
    public ExternalDataController(ExternalDataService externalDataService) {
        this.externalDataService = externalDataService;
    }

    /**
     * Get all available collections
     */
    @GetMapping("/collections")
    public ResponseEntity<Set<String>> getCollections() {
        logger.info("Getting available collections");
        Set<String> collections = externalDataService.getAvailableCollections();
        return ResponseEntity.ok(collections);
    }

    /**
     * Get all data from a collection
     */
    @GetMapping("/collections/{collection}")
    public ResponseEntity<List<Map<String, Object>>> getCollection(@PathVariable String collection) {
        logger.info("Getting all data from collection: {}", collection);
        List<Map<String, Object>> data = externalDataService.findByCollection(collection);
        return ResponseEntity.ok(data);
    }

    /**
     * Find data in collection by key-value filter
     */
    @GetMapping("/collections/{collection}/search")
    public ResponseEntity<List<Map<String, Object>>> searchCollection(
            @PathVariable String collection,
            @RequestParam String key,
            @RequestParam String value) {
        
        logger.info("Searching collection '{}' for key '{}' = '{}'", collection, key, value);
        List<Map<String, Object>> data = externalDataService.findByCollectionAndKey(collection, key, value);
        return ResponseEntity.ok(data);
    }

    /**
     * Find data in collection where key exists
     */
    @GetMapping("/collections/{collection}/has-key")
    public ResponseEntity<List<Map<String, Object>>> findByKeyExists(
            @PathVariable String collection,
            @RequestParam String key) {
        
        logger.info("Finding entries in collection '{}' where key '{}' exists", collection, key);
        List<Map<String, Object>> data = externalDataService.findByCollectionAndKeyExists(collection, key);
        return ResponseEntity.ok(data);
    }

    /**
     * Get cache statistics
     */
    @GetMapping("/cache/stats")
    public ResponseEntity<CacheStatistics> getCacheStatistics() {
        logger.info("Getting cache statistics");
        CacheStatistics stats = externalDataService.getCacheStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Invalidate cache for specific collection
     */
    @DeleteMapping("/cache/collections/{collection}")
    public ResponseEntity<Void> invalidateCollectionCache(@PathVariable String collection) {
        logger.info("Invalidating cache for collection: {}", collection);
        externalDataService.invalidateCache(collection);
        return ResponseEntity.ok().build();
    }

    /**
     * Invalidate all caches
     */
    @DeleteMapping("/cache")
    public ResponseEntity<Void> invalidateAllCaches() {
        logger.info("Invalidating all caches");
        externalDataService.invalidateAllCaches();
        return ResponseEntity.ok().build();
    }

    /**
     * Warm up cache for specific collection
     */
    @PostMapping("/cache/collections/{collection}/warm-up")
    public ResponseEntity<Void> warmUpCache(@PathVariable String collection) {
        logger.info("Warming up cache for collection: {}", collection);
        externalDataService.warmUpCache(collection);
        return ResponseEntity.ok().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Set<String> collections = externalDataService.getAvailableCollections();
        CacheStatistics stats = externalDataService.getCacheStatistics();
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "collectionsAvailable", collections.size(),
            "collections", collections,
            "cacheHitRatio", stats.getOverallHitRatio(),
            "totalCacheHits", stats.getTotalHitCount(),
            "totalCacheMisses", stats.getTotalMissCount()
        );
        
        return ResponseEntity.ok(health);
    }
}