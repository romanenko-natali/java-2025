package ua.university.model;

import ua.university.exception.InvalidDataException;
import ua.university.util.SubjectUtils;
import java.util.logging.Logger;
import java.util.logging.Level;

public record Subject(
        String name,
        int credits
) {
    private static final Logger logger = Logger.getLogger(Subject.class.getName());

    public Subject {
        if (name != null) {
            name = name.trim();
        }

        if (!SubjectUtils.isValidName(name)) {
            String errorMsg = "Invalid subject name: '" + name + "'";
            logger.log(Level.SEVERE, errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        if (!SubjectUtils.isValidCredit(credits)) {
            String errorMsg = "Invalid credit amount: " + credits + " (must be 1-5)";
            logger.log(Level.SEVERE, errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        logger.log(Level.INFO, "Subject created successfully: {0} with {1} credits",
                new Object[]{name, credits});
    }

    public String getDifficultyLevel() {
        if (credits < 1 || credits > 5) {
            String errorMsg = "Invalid credits value for difficulty calculation: " + credits;
            logger.log(Level.WARNING, errorMsg);
            throw new InvalidDataException(errorMsg);
        }

        String difficulty = switch (credits) {
            case 1, 2 -> "Easy";
            case 3, 4 -> "Medium";
            case 5 -> "Hard";
            default -> throw new InvalidDataException("Unexpected credits value: " + credits);
        };

        logger.log(Level.FINE, "Difficulty level calculated: {0} for {1} credits",
                new Object[]{difficulty, credits});
        return difficulty;
    }
}