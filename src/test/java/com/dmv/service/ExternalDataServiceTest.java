package com.dmv.service;

import com.dmv.cache.MemoryCacheLayer;
import com.dmv.cache.NetworkCacheLayer;
import com.dmv.model.CacheStatistics;
import com.dmv.service.impl.ExternalDataServiceImpl;
import com.dmv.storage.JsonFileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalDataServiceTest {

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
    void testFindByCollectionAndKey_MemoryCacheHit() {
        // Given
        String collection = "states";
        String key = "code";
        String value = "CA";
        List<Map<String, Object>> expectedData = createSampleStateData();
        
        when(memoryCache.get(anyString())).thenReturn(Optional.of(expectedData));

        // When
        List<Map<String, Object>> result = externalDataService.findByCollectionAndKey(collection, key, value);

        // Then
        assertEquals(expectedData, result);
        verify(memoryCache).get(anyString());
        verify(networkCache, never()).get(anyString());
        verify(jsonStorage, never()).findByKey(anyString(), anyString(), any());
    }

    @Test
    void testFindByCollectionAndKey_NetworkCacheHit() {
        // Given
        String collection = "states";
        String key = "code";
        String value = "CA";
        List<Map<String, Object>> expectedData = createSampleStateData();
        
        when(memoryCache.get(anyString())).thenReturn(Optional.empty());
        when(networkCache.get(anyString())).thenReturn(Optional.of(expectedData));

        // When
        List<Map<String, Object>> result = externalDataService.findByCollectionAndKey(collection, key, value);

        // Then
        assertEquals(expectedData, result);
        verify(memoryCache).get(anyString());
        verify(networkCache).get(anyString());
        verify(memoryCache).put(anyString(), eq(expectedData)); // Should populate memory cache
        verify(jsonStorage, never()).findByKey(anyString(), anyString(), any());
    }

    @Test
    void testFindByCollectionAndKey_JsonStorageHit() {
        // Given
        String collection = "states";
        String key = "code";
        String value = "CA";
        List<Map<String, Object>> expectedData = createSampleStateData();
        
        when(memoryCache.get(anyString())).thenReturn(Optional.empty());
        when(networkCache.get(anyString())).thenReturn(Optional.empty());
        when(jsonStorage.findByKey(collection, key, value)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = externalDataService.findByCollectionAndKey(collection, key, value);

        // Then
        assertEquals(expectedData, result);
        verify(memoryCache).get(anyString());
        verify(networkCache).get(anyString());
        verify(jsonStorage).findByKey(collection, key, value);
        verify(networkCache).put(anyString(), eq(expectedData)); // Should populate network cache
        verify(memoryCache).put(anyString(), eq(expectedData)); // Should populate memory cache
    }

    @Test
    void testFindByCollectionAndKeyExists() {
        // Given
        String collection = "license-classes";
        String key = "restrictions";
        List<Map<String, Object>> expectedData = createSampleLicenseClassData();
        
        when(memoryCache.get(anyString())).thenReturn(Optional.empty());
        when(networkCache.get(anyString())).thenReturn(Optional.empty());
        when(jsonStorage.findByKeyExists(collection, key)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = externalDataService.findByCollectionAndKeyExists(collection, key);

        // Then
        assertEquals(expectedData, result);
        verify(jsonStorage).findByKeyExists(collection, key);
    }

    @Test
    void testFindByCollection() {
        // Given
        String collection = "fee-schedules";
        List<Map<String, Object>> expectedData = createSampleFeeData();
        
        when(memoryCache.get(anyString())).thenReturn(Optional.empty());
        when(networkCache.get(anyString())).thenReturn(Optional.empty());
        when(jsonStorage.loadCollection(collection)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = externalDataService.findByCollection(collection);

        // Then
        assertEquals(expectedData, result);
        verify(jsonStorage).loadCollection(collection);
    }

    @Test
    void testInvalidateCache() {
        // When
        externalDataService.invalidateCache("states");

        // Then
        verify(memoryCache).invalidateAll();
        verify(networkCache).invalidateAll();
    }

    @Test
    void testInvalidateAllCaches() {
        // When
        externalDataService.invalidateAllCaches();

        // Then
        verify(memoryCache).invalidateAll();
        verify(networkCache).invalidateAll();
    }

    @Test
    void testGetCacheStatistics() {
        // Given
        com.dmv.cache.CacheStats memoryStats = new com.dmv.cache.CacheStats("memoryCache", 10, 5, 2, 100, 1000);
        com.dmv.cache.CacheStats networkStats = new com.dmv.cache.CacheStats("networkCache", 20, 10, 5, 500, 5000);
        
        when(memoryCache.getStats()).thenReturn(memoryStats);
        when(networkCache.getStats()).thenReturn(networkStats);

        // When
        CacheStatistics stats = externalDataService.getCacheStatistics();

        // Then
        assertNotNull(stats);
        assertEquals(30, stats.getTotalHitCount()); // 10 + 20
        assertEquals(15, stats.getTotalMissCount()); // 5 + 10
        assertEquals(7, stats.getTotalEvictionCount()); // 2 + 5
        assertEquals(2, stats.getLayerStats().size());
    }

    @Test
    void testGetAvailableCollections() {
        // Given
        Set<String> expectedCollections = Set.of("states", "license-classes", "fee-schedules");
        when(jsonStorage.getAvailableCollections()).thenReturn(expectedCollections);

        // When
        Set<String> result = externalDataService.getAvailableCollections();

        // Then
        assertEquals(expectedCollections, result);
        verify(jsonStorage).getAvailableCollections();
    }

    @Test
    void testWarmUpCache() {
        // Given
        String collection = "states";
        List<Map<String, Object>> data = createSampleStateData();
        
        when(memoryCache.get(anyString())).thenReturn(Optional.empty());
        when(networkCache.get(anyString())).thenReturn(Optional.empty());
        when(jsonStorage.loadCollection(collection)).thenReturn(data);

        // When
        externalDataService.warmUpCache(collection);

        // Then
        verify(jsonStorage).loadCollection(collection);
        verify(networkCache).put(anyString(), eq(data));
        verify(memoryCache).put(anyString(), eq(data));
    }

    private List<Map<String, Object>> createSampleStateData() {
        Map<String, Object> state = new HashMap<>();
        state.put("code", "CA");
        state.put("name", "California");
        state.put("requiresVisionTest", true);
        state.put("renewalFee", 35.0);
        return List.of(state);
    }

    private List<Map<String, Object>> createSampleLicenseClassData() {
        Map<String, Object> licenseClass = new HashMap<>();
        licenseClass.put("class", "CLASS_C");
        licenseClass.put("name", "Class C - Regular Driver License");
        licenseClass.put("restrictions", List.of());
        return List.of(licenseClass);
    }

    private List<Map<String, Object>> createSampleFeeData() {
        Map<String, Object> fee = new HashMap<>();
        fee.put("type", "renewal");
        fee.put("licenseClass", "CLASS_C");
        fee.put("baseFee", 35.0);
        fee.put("totalFee", 42.5);
        return List.of(fee);
    }
}