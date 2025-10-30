package ua.university.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Configuration manager for reading application settings from properties file
 */
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private final Properties properties;
    private final String configFilePath;

    /**
     * Load configuration from default location (config.properties in resources)
     */
    public AppConfig() {
        this("config.properties");
    }

    /**
     * Load configuration from specified file path
     * @param configFilePath Path to the configuration file
     */
    public AppConfig(String configFilePath) {
        this.configFilePath = configFilePath;
        this.properties = new Properties();
        loadProperties();
    }

    /**
     * Load properties from file
     */
    private void loadProperties() {
        // Try to load from file system first
        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
            logger.info("Configuration loaded successfully from file: {}", configFilePath);
        } catch (IOException e) {
            logger.warn("Could not load config from file system: {}. Trying classpath...", configFilePath);

            // Try to load from classpath (resources folder)
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
                if (input == null) {
                    logger.error("Configuration file not found in classpath: {}", configFilePath);
                    throw new RuntimeException("Unable to find configuration file: " + configFilePath);
                }
                properties.load(input);
                logger.info("Configuration loaded successfully from classpath: {}", configFilePath);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load configuration", ex);
            }
        }
    }

    /**
     * Get property value by key
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get JSON file path for specific entity type
     * Combines base path with entity-specific filename
     * @param entityType Entity type (e.g., "courses", "teachers")
     * @return Full path to JSON file
     */
    public String getJsonFilePath(String entityType) {
        String basePath = getBaseDataPath();
        String key = String.format("data.path.%s.json", entityType.toLowerCase());
        String filename = getProperty(key);

        if (filename == null) {
            logger.warn("JSON filename not found for entity: {}. Using default.", entityType);
            filename = String.format("%s.json", entityType.toLowerCase());
        }

        return combinePaths(basePath, filename);
    }

    /**
     * Get YAML file path for specific entity type
     * Combines base path with entity-specific filename
     * @param entityType Entity type (e.g., "courses", "teachers")
     * @return Full path to YAML file
     */
    public String getYamlFilePath(String entityType) {
        String basePath = getBaseDataPath();
        String key = String.format("data.path.%s.yaml", entityType.toLowerCase());
        String filename = getProperty(key);

        if (filename == null) {
            logger.warn("YAML filename not found for entity: {}. Using default.", entityType);
            filename = String.format("%s.yaml", entityType.toLowerCase());
        }

        return combinePaths(basePath, filename);
    }

    /**
     * Get base data directory path
     * @return Base data directory path
     */
    public String getBaseDataPath() {
        return getProperty("data.path.base", "./data");
    }

    /**
     * Combine base path and filename into full path
     * Handles both relative and absolute paths correctly
     * @param basePath Base directory path
     * @param filename Filename or relative path
     * @return Combined path
     */
    private String combinePaths(String basePath, String filename) {
        Path base = Paths.get(basePath);
        Path file = Paths.get(filename);

        // If filename is absolute, use it as is
        if (file.isAbsolute()) {
            return filename;
        }

        // Otherwise combine base path with filename
        return base.resolve(file).toString();
    }

    /**
     * Get integer property value
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Integer value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key {}: {}. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Get boolean property value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Boolean value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    /**
     * Check if a property exists
     * @param key Property key
     * @return true if property exists, false otherwise
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Get all properties
     * @return Properties object
     */
    public Properties getAllProperties() {
        return new Properties(properties);
    }
}