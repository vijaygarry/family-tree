package com.neasaa.familytree.utils;

import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validatePhoneNumber;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.neasaa.base.app.operation.exception.ValidationException;

public class FamilytreeValidationUtilsTest {
	@Test
    void testValidPhoneNumbers() {
		
        assertDoesNotThrow(() -> validatePhoneNumber("1234567890"));
        assertDoesNotThrow(() -> validatePhoneNumber("+123456789012"));
        assertDoesNotThrow(() -> validatePhoneNumber("123 456 7890"));
        assertDoesNotThrow(() -> validatePhoneNumber("123-456-7890"));
        assertDoesNotThrow(() -> validatePhoneNumber("+1 234-567-8901"));
        assertDoesNotThrow(() -> validatePhoneNumber("+91 234-567-8901"));
        assertDoesNotThrow(() -> validatePhoneNumber("+12345678901"));
        assertDoesNotThrow(() -> validatePhoneNumber("+91 571-484-3763"));
    }

    @Test
    void testInvalidPhoneNumbers() {
        assertThrows(ValidationException.class, () -> validatePhoneNumber("abc1234567"));
        assertThrows(ValidationException.class, () -> validatePhoneNumber("+123"));
        assertThrows(ValidationException.class, () -> validatePhoneNumber("123456"));
        assertThrows(ValidationException.class, () -> validatePhoneNumber("+12@34567890"));
    }

    @Test
    void testNullPhoneNumber() {
        assertDoesNotThrow(() -> validatePhoneNumber(null));
    }

}
