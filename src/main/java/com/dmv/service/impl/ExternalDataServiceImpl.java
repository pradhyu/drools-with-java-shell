package com.dmv.service.impl;

import com.dmv.cache.CacheLayer;
import com.dmv.cache.MemoryCacheLayer;
import com.dmv.cache.NetworkCacheLayer;
import com.dmv.model.CacheStatistics;
import com.dmv.service.ExternalDataService;
import com.dmv.storage.JsonFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of ExternalDataService with multi-layer caching
 * Cache hierarchy: Memory Cache -> Network Cache -> JSON Files
 */
@Service
public class ExternalDataServiceImpl implements ExternalDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalDataServiceImpl.class);
    
    private final MemoryCacheLayer memoryCache;
    private final NetworkCacheLayer networkCache;
    private final JsonFileStorageService jsonStorage;

    public ExternalDataServiceImpl(MemoryCacheLayer memoryCache, 
                                  NetworkCacheLayer networkCache,
                                  JsonFileStorageService jsonStorage) {
        this.memoryCache = memoryCache;
        this.networkCache = networkCache;
        this.jsonStorage = jsonStorage;
        
        logger.info("External data service initialized with multi-layer caching");
    }

    @Override
    public List<Map<String, Object>> findByCollectionAndKey(String collection, String key, Object value) {
        String cacheKey = buildCacheKey(collection, key, value);
        
        // Try memory cache first (fastest)
        Optional<Object> memoryCacheResult = memoryCache.get(cacheKey);
        if (memoryCacheResult.isPresent()) {
            logger.debug("Data retrieved from memory cache for key: {}", cacheKey);
            return castToListOfMaps(memoryCacheResult.get());
        }
        
        // Try network cache second
        Optional<Object> networkCacheResult = networkCache.get(cacheKey);
        if (networkCacheResult.isPresent()) {
            logger.debug("Data retrieved from network cache for key: {}", cacheKey);
            List<Map<String, Object>> result = castToListOfMaps(networkCacheResult.get());
            
            // Populate memory cache
            memoryCache.put(cacheKey, result);
            return result;
        }
        
        // Finally, load from JSON files (slowest)
        logger.debug("Loading data from JSON storage for key: {}", cacheKey);
        List<Map<String, Object>> result = jsonStorage.findByKey(collection, key, value);
        
        // Populate both cache layers
        networkCache.put(cacheKey, result);
        memoryCache.put(cacheKey, result);
        
        logger.debug("Loaded {} entries from collection '{}' with key '{}' = '{}'", 
                    result.size(), collection, key, value);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> findByCollectionAndKeyExists(String collection, String key) {
        String cacheKey = buildCacheKey(collection, "EXISTS:" + key, null);
        
        // Try memory cache first
        Optional<Object> memoryCacheResult = memoryCache.get(cacheKey);
        if (memoryCacheResult.isPresent()) {
            logger.debug("Data retrieved from memory cache for key exists: {}", cacheKey);
            return castToListOfMaps(memoryCacheResult.get());
        }
        
        // Try network cache second
        Optional<Object> networkCacheResult = networkCache.get(cacheKey);
        if (networkCacheResult.isPresent()) {
            logger.debug("Data retrieved from network cache for key exists: {}", cacheKey);
            List<Map<String, Object>> result = castToListOfMaps(networkCacheResult.get());
            
            // Populate memory cache
            memoryCache.put(cacheKey, result);
            return result;
        }
        
        // Load from JSON files
        logger.debug("Loading data from JSON storage for key exists: {}", cacheKey);
        List<Map<String, Object>> result = jsonStorage.findByKeyExists(collection, key);
        
        // Populate both cache layers
        networkCache.put(cacheKey, result);
        memoryCache.put(cacheKey, result);
        
        logger.debug("Loaded {} entries from collection '{}' where key '{}' exists", 
                    result.size(), collection, key);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> findByCollection(String collection) {
        String cacheKey = "COLLECTION:" + collection;
        
        // Try memory cache first
        Optional<Object> memoryCacheResult = memoryCache.get(cacheKey);
        if (memoryCacheResult.isPresent()) {
            logger.debug("Full collection retrieved from memory cache: {}", collection);
            return castToListOfMaps(memoryCacheResult.get());
        }
        
        // Try network cache second
        Optional<Object> networkCacheResult = networkCache.get(cacheKey);
        if (networkCacheResult.isPresent()) {
            logger.debug("Full collection retrieved from network cache: {}", collection);
            List<Map<String, Object>> result = castToListOfMaps(networkCacheResult.get());
            
            // Populate memory cache
            memoryCache.put(cacheKey, result);
            return result;
        }
        
        // Load from JSON files
        logger.debug("Loading full collection from JSON storage: {}", collection);
        List<Map<String, Object>> result = jsonStorage.loadCollection(collection);
        
        // Populate both cache layers
        networkCache.put(cacheKey, result);
        memoryCache.put(cacheKey, result);
        
        logger.debug("Loaded {} entries from collection '{}'", result.size(), collection);
        
        return result;
    }

    @Override
    public void invalidateCache(String collection) {
        logger.info("Invalidating cache for collection: {}", collection);
        
        // We need to invalidate all cache keys related to this collection
        // For simplicity, we'll clear all caches when a collection is invalidated
        // In a production system, you might want to track keys by collection
        memoryCache.invalidateAll();
        networkCache.invalidateAll();
        
        logger.info("Cache invalidated for collection: {}", collection);
    }

    @Override
    public void invalidateAllCaches() {
        logger.info("Invalidating all caches");
        
        memoryCache.invalidateAll();
        networkCache.invalidateAll();
        
        logger.info("All caches invalidated");
    }

    @Override
    public CacheStatistics getCacheStatistics() {
        var memoryStats = memoryCache.getStats();
        var networkStats = networkCache.getStats();
        
        return new CacheStatistics(
            memoryStats.getHitCount() + networkStats.getHitCount(),
            memoryStats.getMissCount() + networkStats.getMissCount(),
            memoryStats.getHitRatio(),
            memoryStats.getEvictionCount() + networkStats.getEvictionCount(),
            memoryStats.getSize() + networkStats.getSize(),
            memoryStats.getMaxSize() + networkStats.getMaxSize(),
            Arrays.asList(memoryStats, networkStats)
        );
    }

    @Override
    public Set<String> getAvailableCollections() {
        return jsonStorage.getAvailableCollections();
    }

    @Override
    public void warmUpCache(String collection) {
        logger.info("Warming up cache for collection: {}", collection);
        
        // Load the entire collection to warm up the cache
        findByCollection(collection);
        
        logger.info("Cache warmed up for collection: {}", collection);
    }

    private String buildCacheKey(String collection, String key, Object value) {
        if (value == null) {
            return collection + ":" + key;
        }
        return collection + ":" + key + "=" + value.toString();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castToListOfMaps(Object obj) {
        if (obj instanceof List) {
            return (List<Map<String, Object>>) obj;
        }
        logger.warn("Unexpected cache value type: {}", obj.getClass());
        return new ArrayList<>();
    }
}