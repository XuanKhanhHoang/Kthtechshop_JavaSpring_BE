package com.kth.kthtechshop.decorators.address;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomerAddressValidator implements ConstraintValidator<ValidCustomerAddress, String> {

    private static final String ADDRESS_REGEX = "^-?\\d+,-?\\d+,.+$";

    @Override
    public void initialize(ValidCustomerAddress constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (!value.trim().matches(ADDRESS_REGEX)) {
            return false;
        }
        String[] parts = value.split(",", 3);
        if (parts.length < 3) {
            return false;
        }
        try {
            int provineId = Integer.parseInt(parts[0]);
            int districtId = Integer.parseInt(parts[1]);
            if (provineId < 1 || provineId > 64 || districtId < 0 || districtId > 2000) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
