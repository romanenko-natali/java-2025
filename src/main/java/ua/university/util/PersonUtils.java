package ua.university.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;

import java.util.Objects;

public final class PersonUtils {

    private static final Logger logger = LoggerFactory.getLogger(PersonUtils.class);

    private PersonUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static String capitalizeText(String text) {
        if (text == null) {
            logger.error("Cannot capitalize null text");
            throw new InvalidDataException("Text must not be null");
        }

        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            logger.debug("capitalizeText: input='{}' -> result='{}'", text, "");
            return "";
        }

        String result = trimmed.substring(0, 1).toUpperCase() +
                trimmed.substring(1).toLowerCase();

        logger.debug("capitalizeText: input='{}' -> result='{}'", text, result);
        return result;
    }

    public static String formatName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            logger.error("Invalid name parts: firstName='{}', lastName='{}'", firstName, lastName);
            throw new InvalidDataException("Both first and last names must be provided");
        }

        String formatted = firstName.trim() + " " + lastName.trim();
        logger.debug("formatName: first='{}', last='{}' -> '{}'", firstName, lastName, formatted);
        return formatted;
    }

    public static String formatEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Invalid email provided: '{}'", email);
            throw new InvalidDataException("Email must not be null or empty");
        }

        String formatted = email.toLowerCase().trim();
        logger.debug("formatEmail: input='{}' -> '{}'", email, formatted);
        return formatted;
    }

    public static String generateEmailFromNames(String... names) {
        if (names == null || names.length == 0) {
            logger.error("No names provided for email generation");
            throw new InvalidDataException("At least one name must be provided");
        }

        for (String name : names) {
            if (name == null || name.trim().isEmpty()) {
                logger.error("Invalid name in email generation: '{}'", name);
                throw new InvalidDataException("Names must not be null or empty");
            }
        }

        String email = String.join(".",
                java.util.Arrays.stream(names)
                        .map(n -> n.toLowerCase().trim())
                        .toArray(String[]::new))
                + "@university.edu";

        logger.info("Generated email from names {} -> {}", java.util.Arrays.toString(names), email);
        return email;
    }

    public static void validateName(String name) {
        boolean valid = ValidationHelper.isStringLengthBetween(name, 1, 50);
        logger.debug("validateName('{}') -> {}", name, valid);
        if (!valid) {
            throw new InvalidDataException("Invalid name: must be 1–50 characters");
        }
    }

    public static void validateEmail(String email) {
        boolean valid = ValidationHelper.isStringMatchPattern(
                email,
                "^[\\w.-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$"
        );
        logger.debug("validateEmail('{}') -> {}", email, valid);
        if (!valid) {
            throw new InvalidDataException("Invalid email format: " + email);
        }
    }

//    public static boolean isValidName(String name){
//        return ValidationHelper.isStringLengthBetween(name, 1, 50);
//    }
//
//    public static boolean isValidEmail(String email){
//        return ValidationHelper.isStringMatchPattern(email, "^[\\w.-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");
//    }
}
