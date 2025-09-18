package ua.university.util;

import ua.university.exception.InvalidDataException;
import ua.university.model.Group;

import java.time.LocalDate;

public class GroupUtils {

    private GroupUtils() {
    }

    public static String formatGroupNumber(String groupNumber) {
        if (groupNumber == null) {
            return null;
        }
        return groupNumber.toUpperCase().trim();
    }


    public static boolean isValidStartYear(int year){
        return ValidationHelper.isNumberBetween (year, LocalDate.now().getYear()-10, LocalDate.now().getYear());
    }

    /**
     * Validates the start year of a group.
     *
     * The year must be within the last 10 years, including the current year.
     *
     * @param year the year to validate
     * @throws InvalidDataException if the year is not within the allowed range
     */
    public static void validateStartYear(int year) {
        int currentYear = LocalDate.now().getYear();
        int minYear = currentYear - 10;

        if (!ValidationHelper.isNumberBetween(year, minYear, currentYear)) {
            throw new InvalidDataException(
                    String.format("Invalid start year: %d. Must be between %d and %d.", year, minYear, currentYear)
            );
        }
    }

    /**
     * Validates a specialty string.
     *
     * A valid specialty must meet the following rules:
     * it is non-null, not blank (ignoring leading/trailing whitespace),
     * its length is between 1 and 100 characters,
     * and it contains only letters, spaces, hyphens, or apostrophes.
     *
     * @param specialty the specialty string to validate
     * @return {@code true} if the specialty is valid; {@code false} otherwise
     */
    public static boolean isValidSpecialty(String specialty) {
        return specialty != null &&
                !specialty.trim().isEmpty() &&
                ValidationHelper.isStringLengthBetween(specialty, 1, 100) &&
                specialty.matches("^[a-zA-Z\\s\\-']+$");
    }

    /**
     * Validates a specialty string.
     *
     * A valid specialty must be non-null, not blank (ignoring leading/trailing whitespace),
     * 1-100 characters long, and contain only letters, spaces, hyphens, or apostrophes.
     *
     * @param specialty the specialty string to validate
     * @throws InvalidDataException if the specialty is invalid
     */
    public static void validateSpecialty(String specialty) {
        if (specialty == null) {
            throw new InvalidDataException(
                    "Specialty is null. Must be 1-100 characters long and contain only letters, spaces, hyphens, or apostrophes."
            );
        }

        String trimmed = specialty.trim();

        if (trimmed.isEmpty()) {
            throw new InvalidDataException(
                    String.format("Specialty is blank after trimming. Original value: '%s'", specialty)
            );
        }

        if (!ValidationHelper.isStringMatchPattern(trimmed, "^[a-zA-Z\\s\\-']{1,100}$")) {
            throw new InvalidDataException(
                    String.format(
                            "Invalid specialty: '%s'. Must be 1-100 characters long and contain only letters, spaces, hyphens, or apostrophes.",
                            specialty
                    )
            );
        }
    }


    public static String formatGroupFullNumber(Group group){
        return group.specialty().substring(0, 2).toUpperCase() + group.number() + "-" + group.startYear() % 100;
    }
}