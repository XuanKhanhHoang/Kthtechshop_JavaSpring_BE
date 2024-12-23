package com.kth.kthtechshop.decorators.address;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CustomerAddressValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCustomerAddress {
    String message() default "Invalid address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
