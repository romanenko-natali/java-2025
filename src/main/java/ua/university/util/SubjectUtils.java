package ua.university.util;

public class SubjectUtils {

    private SubjectUtils() {
    }

    public static boolean isValidName(String name){
        return ValidationHelper.isStringLengthBetween(name, 1, 100);
    }

    public static boolean isValidCredit(int credit){
        return ValidationHelper.isNumberBetween (credit, 1, 5);
    }

}