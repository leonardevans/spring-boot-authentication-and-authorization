package com.lemutugi.validation.constraint;

import com.lemutugi.validation.validator.ShortDescriptionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ShortDescriptionValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShortDescription {
    String message() default "Invalid short description";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
