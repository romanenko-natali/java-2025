package ua.university.model;

public enum ExamType {
    EXAM("Іспит", true, 180),
    CREDIT("Залік", false, 60),
    DIFFERENTIATED_CREDIT("Диференційований залік", true, 90),
    COURSEWORK("Курсова робота", true, 240),
    LABORATORY("Лабораторна робота", true, 120);

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
        return this == EXAM || this == DIFFERENTIATED_CREDIT;
    }

    public static ExamType parseExamType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return EXAM;
        }
        try {
            return ExamType.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return EXAM;
        }
    }
}
