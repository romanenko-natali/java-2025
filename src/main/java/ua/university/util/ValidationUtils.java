package ua.university.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ua.university.exception.InvalidDataException;

import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private ValidationUtils() {
    }

    /**
     * Validates an object using Hibernate Validator
     *
     * @param object object to validate
     * @param <T> type of object
     * @throws InvalidDataException if validation fails with all error messages
     */
    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> String.format(
                            "%s: invalid value '%s' — %s",
                            v.getPropertyPath(),
                            v.getInvalidValue(),
                            v.getMessage()
                    ))
                    .collect(Collectors.joining("\n"));

            throw new InvalidDataException(errorMessage);
        }
    }
}