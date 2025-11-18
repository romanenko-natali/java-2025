package ua.university.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class StartYearValidator implements ConstraintValidator<ValidStartYear, Integer> {

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        if (year == null) {
            return true;
        }

        int currentYear = LocalDate.now().getYear();
        int minYear = currentYear - 10;

        return year >= minYear && year <= currentYear;

//        if (year < minYear || year > currentYear) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate(
//                    String.format("Invalid start year: %d. Must be between %d and %d.",
//                            year, minYear, currentYear)
//            ).addConstraintViolation();
//            return false;
//        }
//
//        return true;
    }
}


