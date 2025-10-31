package com.dmv.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileStorageServiceTest {

    @TempDir
    Path tempDir;

    private JsonFileStorageService jsonFileStorageService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        
        // Create test JSON files
        createTestStateFile();
        createTestLicenseClassFile();
        
        jsonFileStorageService = new JsonFileStorageService(objectMapper, tempDir.toString());
    }

    @Test
    void testLoadCollection() {
        // When
        List<Map<String, Object>> states = jsonFileStorageService.loadCollection("states");

        // Then
        assertNotNull(states);
        assertEquals(2, states.size());
        
        Map<String, Object> california = states.get(0);
        assertEquals("CA", california.get("code"));
        assertEquals("California", california.get("name"));
        assertEquals(true, california.get("requiresVisionTest"));
    }

    @Test
    void testLoadNonExistentCollection() {
        // When
        List<Map<String, Object>> result = jsonFileStorageService.loadCollection("nonexistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByKey() {
        // When
        List<Map<String, Object>> result = jsonFileStorageService.findByKey("states", "code", "CA");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CA", result.get(0).get("code"));
        assertEquals("California", result.get(0).get("name"));
    }

    @Test
    void testFindByKeyNoMatch() {
        // When
        List<Map<String, Object>> result = jsonFileStorageService.findByKey("states", "code", "XX");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByKeyExists() {
        // When
        List<Map<String, Object>> result = jsonFileStorageService.findByKeyExists("states", "requiresVisionTest");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Both states have this key
    }

    @Test
    void testFindByKeyExistsNoMatch() {
        // When
        List<Map<String, Object>> result = jsonFileStorageService.findByKeyExists("states", "nonexistentKey");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByNestedKey() {
        // When
        List<Map<String, Object>> result = jsonFileStorageService.findByKey("license-classes", "fee.base", 35.0);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CLASS_C", result.get(0).get("class"));
    }

    @Test
    void testFindByNestedKeyExists() {
        // When
        List<Map<String, Object>> result = jsonFileStorageService.findByKeyExists("license-classes", "fee.senior");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Both license classes have fee.senior
    }

    @Test
    void testGetAvailableCollections() {
        // When
        Set<String> collections = jsonFileStorageService.getAvailableCollections();

        // Then
        assertNotNull(collections);
        assertEquals(2, collections.size());
        assertTrue(collections.contains("states"));
        assertTrue(collections.contains("license-classes"));
    }

    @Test
    void testIsCollectionModified() throws IOException, InterruptedException {
        // Given - load collection first to establish baseline
        jsonFileStorageService.loadCollection("states");
        
        // Wait a bit to ensure different timestamp
        Thread.sleep(10);
        
        // Modify the file
        Path statesFile = tempDir.resolve("states.json");
        Files.write(statesFile, "[{\"code\":\"CA\",\"name\":\"California Modified\"}]".getBytes());

        // When
        boolean isModified = jsonFileStorageService.isCollectionModified("states");

        // Then
        assertTrue(isModified);
    }

    @Test
    void testGetCollectionModificationTime() {
        // When
        long modTime = jsonFileStorageService.getCollectionModificationTime("states");

        // Then
        assertTrue(modTime > 0);
    }

    @Test
    void testGetDataDirectory() {
        // When
        String dataDir = jsonFileStorageService.getDataDirectory();

        // Then
        assertEquals(tempDir.toString(), dataDir);
    }

    private void createTestStateFile() throws IOException {
        String statesJson = """
            [
              {
                "code": "CA",
                "name": "California",
                "region": "West",
                "requiresVisionTest": true,
                "renewalFee": 35.0
              },
              {
                "code": "NY",
                "name": "New York",
                "region": "Northeast", 
                "requiresVisionTest": true,
                "renewalFee": 64.5
              }
            ]
            """;
        
        Path statesFile = tempDir.resolve("states.json");
        Files.write(statesFile, statesJson.getBytes());
    }

    private void createTestLicenseClassFile() throws IOException {
        String licenseClassJson = """
            [
              {
                "class": "CLASS_C",
                "name": "Class C - Regular Driver License",
                "minAge": 16,
                "fee": {
                  "base": 35.0,
                  "senior": 25.0
                }
              },
              {
                "class": "LEARNER_PERMIT",
                "name": "Learner's Permit",
                "minAge": 15,
                "fee": {
                  "base": 15.0,
                  "senior": 15.0
                }
              }
            ]
            """;
        
        Path licenseClassFile = tempDir.resolve("license-classes.json");
        Files.write(licenseClassFile, licenseClassJson.getBytes());
    }
}