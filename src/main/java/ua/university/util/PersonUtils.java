package ua.university.util;

public class PersonUtils {

    private PersonUtils() {
    }

    public static String capitalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String trimmed = text.trim();
        return trimmed.substring(0, 1).toUpperCase() +
                trimmed.substring(1).toLowerCase();
    }

    public static String formatName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return "";
        }
        return firstName.trim() + " " + lastName.trim();
    }

    public static String formatEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.toLowerCase().trim();
    }

    public static String generateEmailFromNames(String... names) {
        if (names == null || names.length == 0) {
            return null;
        }

        for (String name : names) {
            if (name == null || name.trim().isEmpty()) {
                return null;
            }
        }

        StringBuilder emailBuilder = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                emailBuilder.append(".");
            }
            emailBuilder.append(names[i].toLowerCase().trim());
        }

        emailBuilder.append("@university.edu");
        return emailBuilder.toString();
    }

    public static boolean isValidName(String name){
        return ValidationHelper.isStringLengthBetween(name, 1, 50);
    }

    public static boolean isValidEmail(String email){
        return ValidationHelper.isStringMatchPattern(email, "^[\\w.-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");
    }
}
