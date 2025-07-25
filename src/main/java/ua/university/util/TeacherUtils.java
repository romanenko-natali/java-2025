package ua.university.util;

public class TeacherUtils {

    private TeacherUtils() {
    }

    public static boolean isValidDepartment(String name){
        return ValidationHelper.isStringLengthBetween(name, 1, 100);
    }

    public static boolean isValidPosition(String position){
        return ValidationHelper.isStringLengthBetween(position, 1, 50);
    }

}