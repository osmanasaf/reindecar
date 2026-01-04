package com.reindecar.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TurkishNationalIdValidator implements ConstraintValidator<ValidTurkishNationalId, String> {

    @Override
    public boolean isValid(String nationalId, ConstraintValidatorContext context) {
        if (nationalId == null || nationalId.isEmpty()) {
            return true;
        }

        if (!isExactly11Digits(nationalId)) {
            return false;
        }

        if (startsWithZero(nationalId)) {
            return false;
        }

        int[] digits = convertToDigitArray(nationalId);

        if (!isValidTenthDigit(digits)) {
            return false;
        }

        return isValidEleventhDigit(digits);
    }

    private boolean isExactly11Digits(String nationalId) {
        return nationalId.matches("\\d{11}");
    }

    private boolean startsWithZero(String nationalId) {
        return nationalId.charAt(0) == '0';
    }

    private int[] convertToDigitArray(String nationalId) {
        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = Character.getNumericValue(nationalId.charAt(i));
        }
        return digits;
    }

    private boolean isValidTenthDigit(int[] digits) {
        int sumOdd = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
        int sumEven = digits[1] + digits[3] + digits[5] + digits[7];
        int expectedTenthDigit = ((sumOdd * 7) - sumEven) % 10;

        if (expectedTenthDigit < 0) {
            expectedTenthDigit += 10;
        }

        return digits[9] == expectedTenthDigit;
    }

    private boolean isValidEleventhDigit(int[] digits) {
        int sumFirst10 = 0;
        for (int i = 0; i < 10; i++) {
            sumFirst10 += digits[i];
        }
        int expectedEleventhDigit = sumFirst10 % 10;

        return digits[10] == expectedEleventhDigit;
    }
}
