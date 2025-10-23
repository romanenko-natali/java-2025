package ua.university.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;

public class StudentUtils {

    private static final Logger logger = LoggerFactory.getLogger(PersonUtils.class);

    private StudentUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String formatStudentId(String id) {
        if (id == null || id.trim().isEmpty()) {
            logger.error("Cannot format null or empty student ID: '{}'", id);
            throw new InvalidDataException("Student ID must not be null or empty");
        }

        String formatted = id.toUpperCase().trim();
        logger.debug("formatStudentId: input='{}' -> formatted='{}'", id, formatted);
        return formatted;
    }

    public static void validateStudentId(String studentId) {
        boolean valid = ValidationHelper.isStringMatchPattern(studentId, "^[A-Z]{3}\\d{3}$");
        logger.debug("validateStudentId('{}') -> {}", studentId, valid);
        if (!valid) {
            throw new InvalidDataException("Invalid student ID: must match pattern 'AAA999'");
        }
    }
}