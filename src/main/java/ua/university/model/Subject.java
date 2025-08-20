package ua.university.model;

import ua.university.util.SubjectUtils;


public record Subject(
        String name,
        int credits,
        String department,
        boolean isCore
) {
    public Subject {
        if (!SubjectUtils.isValidName(name)) {
            System.out.println("❌ ПОМИЛКА Subject: некоректна назва '" + name + "'");
        }
        if (!SubjectUtils.isValidCredit(credits)) {
            System.out.println("❌ ПОМИЛКА Subject: некоректна кількість кредитів " + credits + " (має бути 1-5)");
        }
        if (department == null || department.trim().isEmpty()) {
            System.out.println("❌ ПОМИЛКА Subject: відсутня або порожня назва факультету");
        }

        if (name != null) {
            name = name.trim();
        }
        if (department != null) {
            department = department.trim();
        }

        boolean hasCriticalErrors = !SubjectUtils.isValidName(name) ||
                !SubjectUtils.isValidCredit(credits) ||
                department == null || department.trim().isEmpty();

        if (hasCriticalErrors) {
            System.out.println("⚠️  Subject створений з помилками - перевірте дані!");
        }
    }

    public String getUkrainianName() {
        return name != null ? name : "Невідомий предмет";
    }

    public String getDifficultyLevel() {
        if (credits < 1 || credits > 5) {
            System.out.println("⚠️  Неможливо визначити складність - некоректні кредити: " + credits);
            return "Невідома складність";
        }

        return switch (credits) {
            case 1, 2 -> "Легкий";
            case 3, 4 -> "Середній";
            case 5 -> "Складний";
            default -> "Невідома складність";
        };
    }

}

