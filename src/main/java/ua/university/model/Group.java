package ua.university.model;

import ua.university.util.GroupUtils;

import java.time.LocalDate;

public record Group(
        int number,
        String specialty,
        int startYear,
        int studentsCount
) {
    public Group {
        if (!GroupUtils.isValidSpecialty(specialty)) {
            System.out.println("❌ ПОМИЛКА Group: некоректна спеціальність '" + specialty + "'");
        }
        if (!GroupUtils.isValidStartYear(startYear)) {
            System.out.println("❌ ПОМИЛКА Group: некоректний рік початку " + startYear);
        }
        if (studentsCount < 0 || studentsCount > 35) {
            System.out.println("❌ ПОМИЛКА Group: некоректна кількість студентів " + studentsCount + " (має бути 0-35)");
        }
        if (number <= 0) {
            System.out.println("❌ ПОМИЛКА Group: номер групи має бути додатнім, отримано " + number);
        }

        if (specialty != null) {
            specialty = specialty.trim();
        }

        boolean hasCriticalErrors = !GroupUtils.isValidSpecialty(specialty) ||
                !GroupUtils.isValidStartYear(startYear) ||
                studentsCount < 0 || studentsCount > 35 ||
                number <= 0;

        if (hasCriticalErrors) {
            System.out.println("⚠️  Group створена з помилками - перевірте дані!");
        }
    }

    public int getCurrentYear() {
        return LocalDate.now().getYear() - startYear + 1;
    }

    public String getFullName() {
        if (specialty == null || specialty.length() < 2) {
            System.out.println("⚠️  Неможливо створити повну назву - некоректна спеціальність");
            return "ERROR-" + number + "-" + (startYear % 100);
        }
        return specialty.substring(0, 2).toUpperCase() + number + "-" + (startYear % 100);
    }

    public boolean isGraduated() {
        return getCurrentYear() > 4;
    }

    public String groupInfo() {
        return getFullName();
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getStartYear() {
        return startYear;
    }
}