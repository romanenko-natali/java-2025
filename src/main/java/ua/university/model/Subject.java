package ua.university.model;

import ua.university.util.SubjectUtils;


public record Subject(
        String name,
        int credits
) {
    public Subject {
        if (!SubjectUtils.isValidName(name)) {
            System.out.println("❌ Error when create Subject: incorrect name '" + name + "'");
        }
        if (!SubjectUtils.isValidCredit(credits)) {
            System.out.println("❌ Error when create Subject: incorrect credit amount  " + credits + " (must be 1-5)");
        }

        if (name != null) {
            name = name.trim();
        }

        boolean hasCriticalErrors = !SubjectUtils.isValidName(name) ||
                !SubjectUtils.isValidCredit(credits);

        if (hasCriticalErrors) {
            System.out.println("⚠️  Subject was created with error!");
        }
    }

    public String getDifficultyLevel() {
        if (credits < 1 || credits > 5) {
            System.out.println("⚠️  Invalid value of credits: " + credits);
            return "Unknown difficulty";
        }

        return switch (credits) {
            case 1, 2 -> "Easy";
            case 3, 4 -> "Medium";
            case 5 -> "Hard";
            default -> "Unknown";
        };
    }

}

