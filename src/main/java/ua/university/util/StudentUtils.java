package ua.university.util;

public class StudentUtils {

    private StudentUtils() {
    }

    public static String formatStudentId(String id) {
        if (id == null) {
            return null;
        }
        return id.toUpperCase().trim();
    }

    public static boolean isValidStudentId(String studentId){
        return ValidationHelper.isStringMatchPattern(studentId, "^[A-Z]{3}\\d{3}$");
    }
}