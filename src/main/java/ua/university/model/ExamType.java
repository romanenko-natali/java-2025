package ua.university.model;

import ua.university.exception.InvalidDataException;

import java.util.logging.Logger;

public enum ExamType {
    EXAM("Іспит", true, 180),
    CREDIT("Залік", false, 60),
    DIFFERENTIATED_CREDIT("Диференційований залік", true, 90),
    COURSEWORK("Курсова робота", true, 240),
    LABORATORY("Лабораторна робота", true, 120);

    private static final Logger logger = Logger.getLogger(ExamType.class.getName());

    private final String ukrainianName;
    private final boolean isGraded;
    private final int maxDuration;

    ExamType(String ukrainianName, boolean isGraded, int maxDuration) {
        this.ukrainianName = ukrainianName;
        this.isGraded = isGraded;
        this.maxDuration = maxDuration;
    }

    public String getUkrainianName() {
        return ukrainianName;
    }

    public boolean isGraded() {
        return isGraded;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public boolean isWrittenExam() {
        boolean result = this == EXAM || this == DIFFERENTIATED_CREDIT;
        logger.fine("Checking if " + this.name() + " is written exam: " + result);
        return result;
    }

    public static ExamType parseExamType(String value) throws InvalidDataException {
        logger.info("Attempting to parse ExamType from value: " + value);

        if (value == null) {
            logger.warning("Attempted to parse null ExamType value");
            throw new InvalidDataException("ExamType value cannot be null");
        }

        try {
            ExamType result = ExamType.valueOf(value.toUpperCase().trim());
            logger.info("Successfully parsed ExamType: " + result.name());
            return result;
        } catch (IllegalArgumentException e) {
            logger.severe("Failed to parse ExamType: '" + value + "' - invalid value");
            throw new InvalidDataException("Invalid ExamType: '" + value + "'. Should be one of: " +
                    java.util.Arrays.toString(ExamType.values()) + ", but received: " + value, e);
        }
    }
}
