package com.reindecar.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TurkishNationalIdValidator.class)
@Documented
public @interface ValidTurkishNationalId {
    
    String message() default "Geçersiz TC Kimlik Numarası";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
