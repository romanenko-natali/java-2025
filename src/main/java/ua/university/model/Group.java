package ua.university.model;
import ua.university.exception.InvalidDataException;
import ua.university.util.GroupUtils;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public record Group(
        int number,
        String specialty,
        int startYear
) {
    private static final Logger logger = Logger.getLogger(Group.class.getName());

    public Group {
        if (specialty != null) {
            specialty = specialty.trim();
        }

        if (number <= 0) {
            String errorMsg = "Group number must be positive, got: " + number;
            logger.log(Level.SEVERE, errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        if (!GroupUtils.isValidSpecialty(specialty)) {
            String errorMsg = "Invalid specialty: '" + specialty + "'";
            logger.log(Level.SEVERE, errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        if (!GroupUtils.isValidStartYear(startYear)) {
            String errorMsg = "Invalid start year: " + startYear;
            logger.log(Level.SEVERE, errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        logger.log(Level.INFO, "Group created successfully: {0} {1} started in {2}",
                new Object[]{number, specialty, startYear});
    }

    public int getCurrentYear() {
        int currentYear = LocalDate.now().getYear() - startYear + 1;
        logger.log(Level.FINE, "Calculated current year for group {0}: {1}",
                new Object[]{getFullName(), currentYear});
        return currentYear;
    }

    public String getFullName() {
        if (specialty == null || specialty.length() < 2) {
            String errorMsg = "Cannot create full name - invalid specialty: " + specialty;
            logger.log(Level.WARNING, errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        String fullName = specialty.substring(0, 2).toUpperCase() + number + "-" + (startYear % 100);
        logger.log(Level.FINE, "Generated full name: {0}", fullName);
        return fullName;
    }

    public boolean isGraduated() {
        boolean graduated = getCurrentYear() > 4;
        logger.log(Level.FINE, "Group {0} graduation status: {1}",
                new Object[]{getFullName(), graduated});
        return graduated;
    }

    public String groupInfo() {
        return getFullName();
    }
}
