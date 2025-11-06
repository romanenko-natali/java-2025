package ua.university.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartYearValidator.class)
@Documented
public @interface ValidStartYear {
    String message() default "Invalid start year";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}