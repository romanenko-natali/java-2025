package ua.university.model;

import ua.university.exception.InvalidDataException;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ua.university.util.GroupUtils.validateSpecialty;
import static ua.university.util.GroupUtils.validateStartYear;

public record Group(
        int number,
        String specialty,
        int startYear
) implements Comparable<Group> {

    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    public Group {
        if (specialty != null) {
            specialty = specialty.trim();
        }

        if (number <= 0) {
            String errorMsg = "Group number must be positive, got: " + number;
            throw new InvalidDataException(errorMsg);
        }

        validateSpecialty(specialty);

        validateStartYear(startYear);

        logger.info("Group created successfully: {} {} started in {}", number, specialty, startYear);
    }

    public int getCurrentYear() {
        int currentYear = LocalDate.now().getYear() - startYear + 1;
        logger.debug("Calculated current year for group {}: {}", getFullName(), currentYear);
        return currentYear;
    }

    public String getFullName() {
        if (specialty == null || specialty.length() < 2) {
            String errorMsg = "Cannot create full name - invalid specialty: " + specialty;
            throw new InvalidDataException(errorMsg);
        }

        String fullName = specialty.substring(0, 2).toUpperCase() + number + "-" + (startYear % 100);
        logger.debug("Generated full name: {}", fullName);
        return fullName;
    }

    public boolean isGraduated() {
        boolean graduated = getCurrentYear() > 4;
        logger.debug("Group {} graduation status: {}", getFullName(), graduated);
        return graduated;
    }

    public String groupInfo() {
        return getFullName();
    }

    @Override
    public int compareTo(Group o) {
        return this.getFullName().compareTo(o.getFullName());
    }
}
