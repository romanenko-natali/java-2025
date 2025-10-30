package ua.university.serializer;

import ua.university.exception.DataSerializationException;
import java.util.List;

/**
 * Generic interface for serializing and deserializing data in different formats
 * @param <T> Type of objects to serialize/deserialize
 */
public interface DataSerializer<T> {

    /**
     * Serialize a list of items to a file
     * @param items List of items to serialize
     * @param filePath Path to the output file
     * @throws DataSerializationException if serialization fails
     */
    void serialize(List<T> items, String filePath) throws DataSerializationException;

    /**
     * Deserialize items from a file
     * @param filePath Path to the input file
     * @param clazz Class type for deserialization
     * @return List of deserialized items (never null, empty list if file is empty)
     * @throws DataSerializationException if deserialization fails
     */
    List<T> deserialize(String filePath, Class<T> clazz) throws DataSerializationException;

    /**
     * Get the format name (e.g., "JSON", "YAML")
     * @return Format identifier
     */
    String getFormat();
}