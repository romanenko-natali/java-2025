package ua.university.exception;

/**
 * Custom exception for data serialization/deserialization errors
 * Wraps IOException, JsonProcessingException and other serialization-related exceptions
 */
public class DataSerializationException extends Exception {
    /**
     * Constructs a new exception with no detail message
     */
    public DataSerializationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message
     * @param message the detail message
     */
    public DataSerializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public DataSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause
     * @param cause the cause of this exception
     */
    public DataSerializationException(Throwable cause) {
        super(cause);
    }
}