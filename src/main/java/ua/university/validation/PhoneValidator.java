package ua.university.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    // Поля для зберігання параметрів анотації
    private String region;
    private boolean allowEmpty;
    private PhoneFormat format;
    private String[] allowedPrefixes;

    // Patterns для різних регіонів та форматів
    private static final Map<String, Map<PhoneFormat, Pattern>> PATTERNS = Map.of(
            "UA", Map.of(
                    PhoneFormat.INTERNATIONAL, Pattern.compile("^\\+380\\d{9}$"),
                    PhoneFormat.NATIONAL, Pattern.compile("^0\\d{9}$"),
                    PhoneFormat.LOCAL, Pattern.compile("^\\d{9}$")
            ),
            "US", Map.of(
                    PhoneFormat.INTERNATIONAL, Pattern.compile("^\\+1\\d{10}$"),
                    PhoneFormat.NATIONAL, Pattern.compile("^\\d{10}$")
            ),
            "DE", Map.of(
                    PhoneFormat.INTERNATIONAL, Pattern.compile("^\\+49\\d{10,12}$"),
                    PhoneFormat.NATIONAL, Pattern.compile("^0\\d{9,11}$")
            )
    );

    @Override
    public void initialize(ValidPhone annotation) {
        this.region = annotation.region();
        this.allowEmpty = annotation.allowEmpty();
        this.format = annotation.format();
        this.allowedPrefixes = annotation.allowedPrefixes();

        if (!PATTERNS.containsKey(region)) {
            throw new IllegalArgumentException("Unsupported region: " + region);
        }
        if (!PATTERNS.get(region).containsKey(format)) {
            throw new IllegalArgumentException(
                    "Format " + format + " not supported for region " + region
            );
        }

        // Логування конфігурації для debug
        System.out.println("PhoneValidator initialized: " +
                "region=" + region + ", format=" + format + ", allowEmpty=" + allowEmpty);
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return true;
        }

        if (phone.trim().isEmpty()) {
            return allowEmpty;
        }

        if (allowedPrefixes.length > 0) {
            boolean hasAllowedPrefix = Arrays.stream(allowedPrefixes)
                    .anyMatch(prefix -> phone.startsWith(prefix));
            if (!hasAllowedPrefix) {
                return false;
            }
        }

        Pattern pattern = PATTERNS.get(region).get(format);

        return pattern.matcher(phone.trim()).matches();
    }
}
