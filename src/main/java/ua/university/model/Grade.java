package ua.university.model;

import ua.university.exception.InvalidDataException;

import java.util.logging.Logger;

public enum Grade {
    A(90, 100, 4.0, "Відмінно"),
    B(80, 89, 3.0, "Добре+"),
    C(70, 79, 2.0, "Добре"),
    D(60, 69, 1.0, "Задовільно"),
    E(50, 59, 1.0, "Задовільно-"),
    F(0, 49, 0.0, "Незадовільно");

    private static final Logger logger = Logger.getLogger(ExamType.class.getName());

    private final int minPoints;
    private final int maxPoints;
    private final double gpaPoints;
    private final String description;

    Grade(int minPoints, int maxPoints, double gpaPoints, String description) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.gpaPoints = gpaPoints;
        this.description = description;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public double getGpaPoints() {
        return gpaPoints;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPass() {
        return this != F;
    }

    public static Grade fromPoints(int points) {
        for (Grade grade : values()) {
            if (points >= grade.minPoints && points <= grade.maxPoints) {
                return grade;
            }
        }
        return F;
    }

    public static int toUkrainianGrade(Grade grade) {
        return switch (grade) {
            case A -> 5;
            case B -> 4;
            case C -> 4;
            case D -> 3;
            case E -> 3;
            default -> 2;
        };
    }

    public static Grade parseGrade(String value) throws InvalidDataException {
        logger.info("Attempting to parse Grade from value: " + value);

        if (value == null) {
            logger.warning("Attempted to parse null Grade value");
            throw new InvalidDataException("Grade value cannot be null");
        }

        try {
            Grade result = Grade.valueOf(value.toUpperCase().trim());
            logger.info("Successfully parsed Grade: " + result.name());
            return result;
        } catch (IllegalArgumentException e) {
            logger.severe("Failed to parse Grade: '" + value + "' - invalid value");
            throw new InvalidDataException("Invalid Grade: '" + value + "'. Should be one of: " +
                    java.util.Arrays.toString(Grade.values()) + ", but received: " + value, e);
        }
    }
}