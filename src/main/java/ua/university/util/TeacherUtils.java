package ua.university.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;

public final class TeacherUtils {

    private static final Logger logger = LoggerFactory.getLogger(TeacherUtils.class);

    private TeacherUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static void validateDepartment(String name) {
        if (name == null || !ValidationHelper.isStringLengthBetween(name, 1, 100)) {
            logger.error("Invalid department: '{}'", name);
            throw new InvalidDataException("Department name must be 1–100 characters");
        }
        logger.debug("validateDepartment('{}') passed", name);
    }

    public static void validatePosition(String position) {
        if (position == null || !ValidationHelper.isStringLengthBetween(position, 1, 50)) {
            logger.error("Invalid position: '{}'", position);
            throw new InvalidDataException("Position must be 1–50 characters");
        }
        logger.debug("validatePosition('{}') passed", position);
    }
}
