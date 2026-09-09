package com.neasaa.familytree.utils;

import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateAddress;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validatePhoneNumber;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.operation.family.model.AddressDto;
import org.junit.jupiter.api.Test;

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

  @Test
  void testValidateAddressWithSpecialCharacters() {
    // Valid: space and Hindi chars
    AddressDto validHindi = new AddressDto();
    validHindi.setAddressLine1("1234 दिल्ली रोड");
    validHindi.setCity("मुंबई");
    validHindi.setState("उत्तर प्रदेश");
    validHindi.setCountry("भारत");
    validHindi.setPostalCode("110001");

    assertDoesNotThrow(() -> validateAddress(validHindi));

    // Valid: space in address
    AddressDto validSpace = new AddressDto();
    validSpace.setAddressLine1("123 Main Street");
    validSpace.setCity("New Delhi");
    validSpace.setState("Delhi");
    validSpace.setCountry("India");
    validSpace.setPostalCode("110001");

    assertDoesNotThrow(() -> validateAddress(validSpace));

    // Invalid: '(' in address line
    AddressDto invalidSpecialChar = new AddressDto();
    invalidSpecialChar.setAddressLine1("123 Main (Street)");
    invalidSpecialChar.setCity("New Delhi");
    invalidSpecialChar.setState("Delhi");
    invalidSpecialChar.setCountry("India");
    invalidSpecialChar.setPostalCode("110001");

    assertThrows(ValidationException.class, () -> validateAddress(invalidSpecialChar));
  }
}
