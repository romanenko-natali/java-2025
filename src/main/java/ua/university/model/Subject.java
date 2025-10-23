package ua.university.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.exception.InvalidDataException;
import ua.university.util.SubjectUtils;

public record Subject(String name, int credits) implements Comparable<Subject> {

    private static final Logger logger = LoggerFactory.getLogger(Subject.class);

    public Subject {
        String trimmedName = name != null ? name.trim() : null;

        if (!SubjectUtils.isValidName(trimmedName)) {
            String errorMsg = "Invalid subject name: '" + name + "'";
            logger.error(errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        if (!SubjectUtils.isValidCredit(credits)) {
            String errorMsg = "Invalid credit amount: " + credits + " (must be 1-5)";
            logger.error(errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        name = trimmedName;
        logger.info("Subject created successfully: {} with {} credits", name, credits);
    }

    public String getDifficultyLevel() {
        if (credits < 1 || credits > 5) {
            String errorMsg = "Invalid credits value for difficulty calculation: " + credits;
            logger.warn(errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        String difficulty = switch (credits) {
            case 1, 2 -> "Easy";
            case 3, 4 -> "Medium";
            case 5 -> "Hard";
            default -> throw new InvalidDataException("Unexpected credits value: " + credits);
        };

        logger.debug("Difficulty level calculated: {} for {} credits", difficulty, credits);
        return difficulty;
    }

    @Override
    public int compareTo(Subject other) {
        int nameCompare = this.name().compareTo(other.name());
        if (nameCompare != 0) return nameCompare;
        return Integer.compare(this.credits(), other.credits());
    }

}
