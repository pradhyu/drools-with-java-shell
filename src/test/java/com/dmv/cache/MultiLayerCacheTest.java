package com.dmv.cache;

import com.dmv.model.CacheStatistics;
import com.dmv.service.ExternalDataService;
import com.dmv.service.impl.ExternalDataServiceImpl;
import com.dmv.storage.JsonFileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for multi-layer cache behavior and metrics
 */
@ExtendWith(MockitoExtension.class)
class MultiLayerCacheTest {

    private static final Logger logger = LoggerFactory.getLogger(MultiLayerCacheTest.class);

    @Mock
    private MemoryCacheLayer memoryCache;

    @Mock
    private NetworkCacheLayer networkCache;

    @Mock
    private JsonFileStorageService jsonStorage;

    private ExternalDataService externalDataService;

    @BeforeEach
    void setUp() {
        externalDataService = new ExternalDataServiceImpl(memoryCache, networkCache, jsonStorage);
    }

    @Test
    void testMemoryCacheHit() {
        // Given
        String collection = "states";
        String key = "code";
        String value = "CA";
        List<Map<String, Object>> expectedData = Arrays.asList(
            Map.of("code", "CA", "name", "California")
        );

        // Mock memory cache hit
        when(memoryCache.get(anyString())).thenReturn(java.util.Optional.of(expectedData));

        // When
        List<Map<String, Object>> result = externalDataService.findByCollectionAndKey(collection, key, value);

        // Then
        assertEquals(expectedData, result);
        verify(memoryCache, times(1)).get(anyString());
        verify(networkCache, never()).get(anyString());
        verify(jsonStorage, never()).findByKey(anyString(), anyString(), any());
        
        logger.info("✓ Memory cache hit test passed - only memory cache was accessed");
    }

    @Test
    void testNetworkCacheHitAfterMemoryMiss() {
        // Given
        String collection = "states";
        String key = "code";
        String value = "CA";
        List<Map<String, Object>> expectedData = Arrays.asList(
            Map.of("code", "CA", "name", "California")
        );

        // Mock memory cache miss, network cache hit
        when(memoryCache.get(anyString())).thenReturn(java.util.Optional.empty());
        when(networkCache.get(anyString())).thenReturn(java.util.Optional.of(expectedData));

        // When
        List<Map<String, Object>> result = externalDataService.findByCollectionAndKey(collection, key, value);

        // Then
        assertEquals(expectedData, result);
        verify(memoryCache, times(1)).get(anyString());
        verify(networkCache, times(1)).get(anyString());
        verify(memoryCache, times(1)).put(anyString(), eq(expectedData)); // Should populate memory cache
        verify(jsonStorage, never()).findByKey(anyString(), anyString(), any());
        
        logger.info("✓ Network cache hit test passed - memory miss, network hit, memory populated");
    }

    @Test
    void testStorageAccessAfterBothCacheMisses() {
        // Given
        String collection = "states";
        String key = "code";
        String value = "CA";
        List<Map<String, Object>> expectedData = Arrays.asList(
            Map.of("code", "CA", "name", "California")
        );

        // Mock both cache misses, storage hit
        when(memoryCache.get(anyString())).thenReturn(java.util.Optional.empty());
        when(networkCache.get(anyString())).thenReturn(java.util.Optional.empty());
        when(jsonStorage.findByKey(collection, key, value)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = externalDataService.findByCollectionAndKey(collection, key, value);

        // Then
        assertEquals(expectedData, result);
        verify(memoryCache, times(1)).get(anyString());
        verify(networkCache, times(1)).get(anyString());
        verify(jsonStorage, times(1)).findByKey(collection, key, value);
        verify(networkCache, times(1)).put(anyString(), eq(expectedData)); // Should populate network cache
        verify(memoryCache, times(1)).put(anyString(), eq(expectedData)); // Should populate memory cache
        
        logger.info("✓ Storage access test passed - both caches missed, storage accessed, both caches populated");
    }

    @Test
    void testCacheStatisticsTracking() {
        // Given
        CacheStats memoryStats = new CacheStats("memoryCache", 5, 2, 0, 10, 1000, 
                                               1, 2, java.time.LocalDateTime.now(), 
                                               java.time.LocalDateTime.now(), "MEMORY");
        CacheStats networkStats = new CacheStats("networkCache", 3, 4, 1, 15, 5000,
                                                50, 60, java.time.LocalDateTime.now(),
                                                java.time.LocalDateTime.now(), "NETWORK");

        when(memoryCache.getStats()).thenReturn(memoryStats);
        when(networkCache.getStats()).thenReturn(networkStats);

        // When
        CacheStatistics statistics = externalDataService.getCacheStatistics();

        // Then
        assertNotNull(statistics);
        assertEquals(8, statistics.getTotalHitCount()); // 5 + 3
        assertEquals(6, statistics.getTotalMissCount()); // 2 + 4
        assertEquals(5, statistics.getHitsForLayer("MEMORY"));
        assertEquals(3, statistics.getHitsForLayer("NETWORK"));
        assertEquals(2, statistics.getMissesForLayer("MEMORY"));
        assertEquals(4, statistics.getMissesForLayer("NETWORK"));
        assertEquals(1.0, statistics.getAvgResponseTimeForLayer("MEMORY"));
        assertEquals(50.0, statistics.getAvgResponseTimeForLayer("NETWORK"));
        
        logger.info("✓ Cache statistics tracking test passed - layer-specific metrics verified");
    }

    @Test
    void testCacheInvalidationAcrossLayers() {
        // Given
        String collection = "states";

        // When
        externalDataService.invalidateCache(collection);

        // Then
        verify(memoryCache, times(1)).invalidateAll();
        verify(networkCache, times(1)).invalidateAll();
        
        logger.info("✓ Cache invalidation test passed - all layers invalidated");
    }

    @Test
    void testCacheWarmupPopulatesAllLayers() {
        // Given
        String collection = "states";
        List<Map<String, Object>> expectedData = Arrays.asList(
            Map.of("code", "CA", "name", "California"),
            Map.of("code", "NY", "name", "New York")
        );

        // Mock storage to return data for warmup
        when(memoryCache.get(anyString())).thenReturn(java.util.Optional.empty());
        when(networkCache.get(anyString())).thenReturn(java.util.Optional.empty());
        when(jsonStorage.loadCollection(collection)).thenReturn(expectedData);

        // When
        externalDataService.warmUpCache(collection);

        // Then
        verify(jsonStorage, times(1)).loadCollection(collection);
        verify(networkCache, times(1)).put(anyString(), eq(expectedData));
        verify(memoryCache, times(1)).put(anyString(), eq(expectedData));
        
        logger.info("✓ Cache warmup test passed - all layers populated from storage");
    }

    @Test
    void testCacheLayerPerformanceDifferences() {
        // This test verifies that we can distinguish between cache layers based on performance
        // In a real scenario, memory cache should be fastest, network cache slower, storage slowest
        
        // Given
        CacheStats memoryStats = new CacheStats("memoryCache", 10, 0, 0, 10, 1000,
                                               1, 1, java.time.LocalDateTime.now(),
                                               java.time.LocalDateTime.now(), "MEMORY");
        CacheStats networkStats = new CacheStats("networkCache", 5, 0, 0, 5, 5000,
                                                50, 55, java.time.LocalDateTime.now(),
                                                java.time.LocalDateTime.now(), "NETWORK");

        when(memoryCache.getStats()).thenReturn(memoryStats);
        when(networkCache.getStats()).thenReturn(networkStats);

        // When
        CacheStatistics statistics = externalDataService.getCacheStatistics();

        // Then
        double memoryAvgTime = statistics.getAvgResponseTimeForLayer("MEMORY");
        double networkAvgTime = statistics.getAvgResponseTimeForLayer("NETWORK");
        
        assertTrue(memoryAvgTime < networkAvgTime, 
                  "Memory cache should be faster than network cache");
        assertTrue(memoryAvgTime < 10, "Memory cache should be very fast (< 10ms)");
        assertTrue(networkAvgTime > 40, "Network cache should have simulated latency (> 40ms)");
        
        logger.info("✓ Cache performance test passed - memory: {}ms, network: {}ms", 
                   memoryAvgTime, networkAvgTime);
    }
}