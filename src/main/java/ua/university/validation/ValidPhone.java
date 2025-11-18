package ua.university.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface ValidPhone {


    String message() default "Invalid phone number format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    String region() default "UA";

    boolean allowEmpty() default false;

    PhoneFormat format() default PhoneFormat.INTERNATIONAL;

    String[] allowedPrefixes() default {};
}
