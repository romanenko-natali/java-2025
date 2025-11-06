package ua.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.university.util.ValidationUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Subject(
        @NotBlank(message = "Subject name cannot be null or blank")
        @Size(min = 1, max = 100, message = "Subject name must be 1-100 characters long")
        String name,

        @Min(value = 1, message = "Credits must be at least 1")
        @Max(value = 5, message = "Credits must be at most 5")
        int credits
) implements Comparable<Subject> {

    private static final Logger logger = LoggerFactory.getLogger(Subject.class);

    public Subject {
        name = name != null ? name.trim() : null;
        logger.info("Subject created: {} with {} credits", name, credits);
    }

    public static Subject createValidSubject(String name, int credits) {
        Subject subject = new Subject(name, credits);
        ValidationUtils.validate(subject);
        logger.info("Valid subject created: {} with {} credits", name, credits);
        return subject;
    }

    public String getDifficultyLevel() {
        String difficulty = switch (credits) {
            case 1, 2 -> "Easy";
            case 3, 4 -> "Medium";
            case 5 -> "Hard";
            default -> throw new IllegalStateException("Unexpected credits value: " + credits);
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