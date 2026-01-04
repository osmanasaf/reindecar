package com.reindecar.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TurkishTaxNumberValidator implements ConstraintValidator<ValidTurkishTaxNumber, String> {

    @Override
    public boolean isValid(String taxNumber, ConstraintValidatorContext context) {
        if (taxNumber == null || taxNumber.isEmpty()) {
            return true;
        }

        if (!isExactly10Digits(taxNumber)) {
            return false;
        }

        int[] digits = convertToDigitArray(taxNumber);
        int calculatedCheckDigit = calculateCheckDigit(digits);

        return digits[9] == calculatedCheckDigit;
    }

    private boolean isExactly10Digits(String taxNumber) {
        return taxNumber.matches("\\d{10}");
    }

    private int[] convertToDigitArray(String taxNumber) {
        int[] digits = new int[10];
        for (int i = 0; i < 10; i++) {
            digits[i] = Character.getNumericValue(taxNumber.charAt(i));
        }
        return digits;
    }

    private int calculateCheckDigit(int[] digits) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = (digits[i] + (9 - i)) % 10;
            sum += (digit * (int) Math.pow(2, 9 - i)) % 9;
            
            if (digit != 0 && (digit * (int) Math.pow(2, 9 - i)) % 9 == 0) {
                sum += 9;
            }
        }

        return (10 - (sum % 10)) % 10;
    }
}
