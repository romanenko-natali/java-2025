package ua.university.model;
import ua.university.util.GroupUtils;

import java.time.LocalDate;

public record Group(
        int number,
        String specialty,
        int startYear
) {
    public Group {
        if (!GroupUtils.isValidSpecialty(specialty)) {
            System.out.println("❌ ERROR Group: invalid specialty '" + specialty + "'");
        }
        if (!GroupUtils.isValidStartYear(startYear)) {
            System.out.println("❌ ERROR Group: invalid start year " + startYear);
        }
        if (number <= 0) {
            System.out.println("❌ ERROR Group: group number must be positive, got " + number);
        }

        if (specialty != null) {
            specialty = specialty.trim();
        }

        boolean hasCriticalErrors = !GroupUtils.isValidSpecialty(specialty) ||
                !GroupUtils.isValidStartYear(startYear) ||
                number <= 0;

        if (hasCriticalErrors) {
            System.out.println("⚠️  Group created with errors – please check the data!");
        }
    }

    public int getCurrentYear() {
        return LocalDate.now().getYear() - startYear + 1;
    }

    public String fullName() {
        return specialty.substring(0, 2).toUpperCase() + "-" + number + "-" + (startYear % 100);
    }

    public boolean isGraduated() {
        return getCurrentYear() > 4;
    }

    public String groupInfo() {
        return fullName();
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getStartYear() {
        return startYear;
    }

}
