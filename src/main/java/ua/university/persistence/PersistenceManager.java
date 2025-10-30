package ua.university.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.config.AppConfig;
import ua.university.exception.DataSerializationException;
import ua.university.serializer.DataSerializer;
import ua.university.serializer.JsonDataSerializer;
import ua.university.serializer.YamlDataSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager for data persistence operations
 * Handles saving and loading data in different formats (JSON, YAML)
 */
public class PersistenceManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

    private final AppConfig config;
    private final Map<String, DataSerializer<?>> serializers;

    /**
     * Constructor with configuration
     * @param config Application configuration
     */
    public PersistenceManager(AppConfig config) {
        this.config = config;
        this.serializers = new HashMap<>();
        initializeSerializers();
        logger.info("RepositoryManager initialized with {} serializers", serializers.size());
    }

    /**
     * Initialize available serializers
     */
    private void initializeSerializers() {
        serializers.put("JSON", new JsonDataSerializer<>());
        serializers.put("YAML", new YamlDataSerializer<>());
        logger.debug("Registered serializers: {}", serializers.keySet());
    }

    /**
     * Save list to file in specified format
     * @param items List of items to save
     * @param entityType Entity type name (e.g., "courses", "teachers")
     * @param clazz Class type of entities
     * @param format Format to use ("JSON" or "YAML")
     * @param <T> Type of entities
     * @throws DataSerializationException if save operation fails
     */
    public <T> void save(List<T> items,
                         String entityType,
                         Class<T> clazz,
                         String format) throws DataSerializationException {
        validateParameters(items, entityType, clazz);

        String formatUpper = format.toUpperCase();
        DataSerializer<T> serializer = getSerializer(formatUpper);
        String filePath = getFilePath(entityType, formatUpper);

        logger.info("Saving {} items of type {} to {} file: {}",
                items.size(), entityType, formatUpper, filePath);

        try {
            serializer.serialize(items, filePath);
            logger.info("Successfully saved {} {} items to {}", items.size(), entityType, formatUpper);
        } catch (DataSerializationException e) {
            logger.error("Failed to save {} to {}: {}", entityType, formatUpper, e.getMessage());
            throw e;
        }
    }

    /**
     * Load list from file in specified format
     * @param entityType Entity type name (e.g., "courses", "teachers")
     * @param clazz Class type of entities
     * @param format Format to use ("JSON" or "YAML")
     * @param <T> Type of entities
     * @return List of loaded items
     * @throws DataSerializationException if load operation fails
     */
    public <T> List<T> load(String entityType,
                            Class<T> clazz,
                            String format) throws DataSerializationException {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new DataSerializationException("Entity type cannot be null or empty");
        }

        if (clazz == null) {
            throw new DataSerializationException("Class type cannot be null");
        }

        String formatUpper = format.toUpperCase();
        DataSerializer<T> serializer = getSerializer(formatUpper);
        String filePath = getFilePath(entityType, formatUpper);

        logger.info("Loading {} from {} file: {}", entityType, formatUpper, filePath);

        try {
            List<T> items = serializer.deserialize(filePath, clazz);
            logger.info("Successfully loaded {} items of type {}", items.size(), entityType);
            return items;
        } catch (DataSerializationException e) {
            logger.error("Failed to load {} from {}: {}", entityType, formatUpper, e.getMessage());
            throw e;
        }
    }

    /**
     * Save list to both JSON and YAML formats
     * @param items List of items to save
     * @param entityType Entity type name
     * @param clazz Class type of entities
     * @param <T> Type of entities
     * @throws DataSerializationException if any save operation fails
     */
    public <T> void saveAllFormats(List<T> items,
                                   String entityType,
                                   Class<T> clazz) throws DataSerializationException {
        logger.info("Saving {} items of type {} to all formats", items.size(), entityType);

        save(items, entityType, clazz, "JSON");
        save(items, entityType, clazz, "YAML");

        logger.info("Successfully saved {} to all formats", entityType);
    }

    /**
     * Validate common parameters
     */
    private <T> void validateParameters(List<T> items, String entityType, Class<T> clazz)
            throws DataSerializationException {
        if (items == null) {
            throw new DataSerializationException("Items list cannot be null");
        }

        if (entityType == null || entityType.trim().isEmpty()) {
            throw new DataSerializationException("Entity type cannot be null or empty");
        }

        if (clazz == null) {
            throw new DataSerializationException("Class type cannot be null");
        }
    }

    /**
     * Get serializer for specified format
     * @param format Format name (JSON, YAML)
     * @param <T> Type parameter
     * @return DataSerializer instance
     * @throws DataSerializationException if format is not supported
     */
    @SuppressWarnings("unchecked")
    private <T> DataSerializer<T> getSerializer(String format) throws DataSerializationException {
        DataSerializer<?> serializer = serializers.get(format);

        if (serializer == null) {
            String errorMsg = String.format("Unsupported format: %s. Available formats: %s",
                    format, serializers.keySet());
            logger.error(errorMsg);
            throw new DataSerializationException(errorMsg);
        }

        return (DataSerializer<T>) serializer;
    }

    /**
     * Get file path for entity type and format
     * @param entityType Entity type name
     * @param format Format (JSON or YAML)
     * @return File path from configuration
     */
    private String getFilePath(String entityType, String format) {
        return switch (format) {
            case "JSON" -> config.getJsonFilePath(entityType);
            case "YAML" -> config.getYamlFilePath(entityType);
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    /**
     * Check if format is supported
     * @param format Format name
     * @return true if supported, false otherwise
     */
    public boolean isFormatSupported(String format) {
        return serializers.containsKey(format.toUpperCase());
    }

    /**
     * Get list of supported formats
     * @return Array of supported format names
     */
    public String[] getSupportedFormats() {
        return serializers.keySet().toArray(new String[0]);
    }
}