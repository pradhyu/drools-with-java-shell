package com.dmv.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for reading JSON collections from files
 * Supports collection name mapping from filenames and key-based filtering
 */
@Service
public class JsonFileStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonFileStorageService.class);
    
    private final ObjectMapper objectMapper;
    private final String dataDirectory;
    private final Map<String, Long> fileModificationTimes = new ConcurrentHashMap<>();
    private final WatchService watchService;
    private final Path dataPath;

    public JsonFileStorageService(ObjectMapper objectMapper, 
                                 @Value("${dmv.external-data.directory:src/main/resources/data}") String dataDirectory) throws IOException {
        this.objectMapper = objectMapper;
        this.dataDirectory = dataDirectory;
        this.dataPath = Paths.get(dataDirectory);
        
        // Create data directory if it doesn't exist
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
            logger.info("Created data directory: {}", dataPath.toAbsolutePath());
        }
        
        // Initialize file watcher
        this.watchService = FileSystems.getDefault().newWatchService();
        dataPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, 
                         StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
        
        logger.info("JSON file storage service initialized with directory: {}", dataPath.toAbsolutePath());
    }

    /**
     * Load all data from a collection (JSON file)
     * @param collection Collection name (filename without .json extension)
     * @return List of maps representing JSON objects
     */
    public List<Map<String, Object>> loadCollection(String collection) {
        String filename = collection + ".json";
        Path filePath = dataPath.resolve(filename);
        
        if (!Files.exists(filePath)) {
            logger.debug("Collection file not found: {}", filePath);
            return new ArrayList<>();
        }
        
        try {
            // Check if file has been modified
            long lastModified = Files.getLastModifiedTime(filePath).toMillis();
            fileModificationTimes.put(collection, lastModified);
            
            List<Map<String, Object>> data = objectMapper.readValue(
                filePath.toFile(), 
                new TypeReference<List<Map<String, Object>>>() {}
            );
            
            logger.debug("Loaded {} entries from collection: {}", data.size(), collection);
            return data;
            
        } catch (IOException e) {
            logger.error("Error loading collection: {}", collection, e);
            return new ArrayList<>();
        }
    }

    /**
     * Find entries in a collection by key-value filter
     * @param collection Collection name
     * @param key Key to filter by
     * @param value Value to match
     * @return List of matching entries
     */
    public List<Map<String, Object>> findByKey(String collection, String key, Object value) {
        List<Map<String, Object>> allData = loadCollection(collection);
        
        return allData.stream()
            .filter(entry -> matchesKeyValue(entry, key, value))
            .collect(Collectors.toList());
    }

    /**
     * Find entries in a collection where the specified key exists
     * @param collection Collection name
     * @param key Key that must exist in the entry
     * @return List of entries containing the key
     */
    public List<Map<String, Object>> findByKeyExists(String collection, String key) {
        List<Map<String, Object>> allData = loadCollection(collection);
        
        return allData.stream()
            .filter(entry -> hasKey(entry, key))
            .collect(Collectors.toList());
    }

    /**
     * Get all available collections (JSON files in the data directory)
     * @return Set of collection names
     */
    public Set<String> getAvailableCollections() {
        try {
            return Files.list(dataPath)
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> {
                    String filename = path.getFileName().toString();
                    return filename.substring(0, filename.lastIndexOf('.'));
                })
                .collect(Collectors.toSet());
        } catch (IOException e) {
            logger.error("Error listing collections", e);
            return new HashSet<>();
        }
    }

    /**
     * Check if a collection file has been modified since last load
     * @param collection Collection name
     * @return true if file has been modified
     */
    public boolean isCollectionModified(String collection) {
        String filename = collection + ".json";
        Path filePath = dataPath.resolve(filename);
        
        if (!Files.exists(filePath)) {
            return false;
        }
        
        try {
            long lastModified = Files.getLastModifiedTime(filePath).toMillis();
            Long cachedTime = fileModificationTimes.get(collection);
            
            return cachedTime == null || lastModified > cachedTime;
        } catch (IOException e) {
            logger.error("Error checking file modification time for collection: {}", collection, e);
            return true; // Assume modified if we can't check
        }
    }

    /**
     * Get file modification time for a collection
     * @param collection Collection name
     * @return Last modification time in milliseconds, or 0 if file doesn't exist
     */
    public long getCollectionModificationTime(String collection) {
        String filename = collection + ".json";
        Path filePath = dataPath.resolve(filename);
        
        try {
            if (Files.exists(filePath)) {
                return Files.getLastModifiedTime(filePath).toMillis();
            }
        } catch (IOException e) {
            logger.error("Error getting modification time for collection: {}", collection, e);
        }
        
        return 0;
    }

    private boolean matchesKeyValue(Map<String, Object> entry, String key, Object value) {
        if (key.contains(".")) {
            // Handle nested keys with dot notation
            return matchesNestedKeyValue(entry, key, value);
        } else {
            // Simple key matching
            Object entryValue = entry.get(key);
            return Objects.equals(entryValue, value);
        }
    }

    private boolean hasKey(Map<String, Object> entry, String key) {
        if (key.contains(".")) {
            // Handle nested keys with dot notation
            return hasNestedKey(entry, key);
        } else {
            // Simple key existence check
            return entry.containsKey(key);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean matchesNestedKeyValue(Map<String, Object> entry, String key, Object value) {
        String[] keyParts = key.split("\\.");
        Object current = entry;
        
        for (String part : keyParts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return false;
            }
            
            if (current == null) {
                return false;
            }
        }
        
        return Objects.equals(current, value);
    }

    @SuppressWarnings("unchecked")
    private boolean hasNestedKey(Map<String, Object> entry, String key) {
        String[] keyParts = key.split("\\.");
        Object current = entry;
        
        for (String part : keyParts) {
            if (current instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) current;
                if (!map.containsKey(part)) {
                    return false;
                }
                current = map.get(part);
            } else {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get the data directory path
     * @return Path to data directory
     */
    public String getDataDirectory() {
        return dataDirectory;
    }
}