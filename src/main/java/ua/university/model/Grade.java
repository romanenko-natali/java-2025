package ua.university.model;

public enum Grade {
    A(90, 100, 4.0, "Відмінно"),
    B(82, 89, 3.0, "Добре"),
    C(74, 81, 2.0, "Задовільно"),
    D(64, 73, 1.0, "Задовільно"),
    F(0, 63, 0.0, "Незадовільно");

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
        return F; // fallback
    }

    public static Grade fromUkrainianGrade(int ukrainianGrade) {
        return switch (ukrainianGrade) {
            case 5 -> A;
            case 4 -> B;
            case 3 -> C;
            case 2 -> D;
            case 1 -> F;
            default -> F;
        };
    }

    public static Grade parseGrade(String value) {
        if (value == null || value.trim().isEmpty()) {
            return F;
        }
        try {
            return Grade.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return F;
        }
    }
}