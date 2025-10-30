package ua.university.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON implementation of DataSerializer using Jackson library
 * @param <T> Type of objects to serialize/deserialize
 */
public class JsonDataSerializer<T> extends AbstractDataSerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(JsonDataSerializer.class);

    /**
     * Constructor with default ObjectMapper configuration
     */
    public JsonDataSerializer() {
        super(createDefaultObjectMapper());
        logger.debug("JsonDataSerializer initialized with pretty printing enabled");
    }

    /**
     * Constructor with custom ObjectMapper (for testing or custom configuration)
     * @param objectMapper Custom configured ObjectMapper
     */
    public JsonDataSerializer(ObjectMapper objectMapper) {
        super(objectMapper);
        logger.debug("JsonDataSerializer initialized with custom ObjectMapper");
    }

    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Override
    public String getFormat() {
        return "JSON";
    }
}