package ua.university.util;

import ua.university.model.Group;

import java.time.LocalDate;

public class GroupUtils {

    private GroupUtils() {
    }

    public static String formatGroupNumber(String groupNumber) {
        if (groupNumber == null) {
            return null;
        }
        return groupNumber.toUpperCase().trim();
    }

    public static boolean isValidSpecialty(String specialty){
        return ValidationHelper.isStringLengthBetween(specialty, 1, 100);
    }

    public static boolean isValidStartYear(int year){
        return ValidationHelper.isNumberBetween (year, LocalDate.now().getYear()-10, LocalDate.now().getYear()+10);
    }

    public static String formatGroupFullNumber(Group group){
        return group.specialty().substring(0, 2).toUpperCase() + group.number() + "-" + group.startYear() % 100;
    }
}