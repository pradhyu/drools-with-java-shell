package com.dmv.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryCacheLayerTest {

    @Mock
    private CacheManager cacheManager;
    
    @Mock
    private Cache cache;

    private MemoryCacheLayer memoryCacheLayer;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache("memoryCache")).thenReturn(cache);
        memoryCacheLayer = new MemoryCacheLayer(cacheManager);
    }

    @Test
    void testCacheHit() {
        // Given
        String key = "test-key";
        String value = "test-value";
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn(value);
        when(cache.get(key)).thenReturn(wrapper);

        // When
        Optional<Object> result = memoryCacheLayer.get(key);

        // Then
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
        verify(cache).get(key);
    }

    @Test
    void testCacheMiss() {
        // Given
        String key = "missing-key";
        when(cache.get(key)).thenReturn(null);

        // When
        Optional<Object> result = memoryCacheLayer.get(key);

        // Then
        assertFalse(result.isPresent());
        verify(cache).get(key);
    }

    @Test
    void testPutValue() {
        // Given
        String key = "test-key";
        String value = "test-value";

        // When
        memoryCacheLayer.put(key, value);

        // Then
        verify(cache).put(key, value);
    }

    @Test
    void testInvalidateKey() {
        // Given
        String key = "test-key";

        // When
        memoryCacheLayer.invalidate(key);

        // Then
        verify(cache).evict(key);
    }

    @Test
    void testInvalidateAll() {
        // When
        memoryCacheLayer.invalidateAll();

        // Then
        verify(cache).clear();
    }

    @Test
    void testGetStats() {
        // When
        CacheStats stats = memoryCacheLayer.getStats();

        // Then
        assertNotNull(stats);
        assertEquals("memoryCache", stats.getCacheName());
        assertEquals(1000, stats.getMaxSize());
    }

    @Test
    void testGetName() {
        // When
        String name = memoryCacheLayer.getName();

        // Then
        assertEquals("memoryCache", name);
    }
}