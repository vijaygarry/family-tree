package com.neasaa.base.app.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.neasaa.base.app.operation.exception.ValidationException;
import org.junit.jupiter.api.Test;

public class PhoneUtilTest {

  // --- normalizePhoneNumber ---

  @Test
  void normalize_null_returnsNull() {
    assertNull(PhoneUtil.normalizePhoneNumber(null));
  }

  @Test
  void normalize_blank_returnsNull() {
    assertNull(PhoneUtil.normalizePhoneNumber("   "));
  }

  @Test
  void normalize_tenDigit_prefixesWith91() {
    assertEquals("919123456789", PhoneUtil.normalizePhoneNumber("9123456789"));
  }

  @Test
  void normalize_formattedTenDigit_prefixesWith91() {
    assertEquals("919123456789", PhoneUtil.normalizePhoneNumber("912 345 6789"));
  }

  @Test
  void normalize_twelvePlusDigitWithCountryCode_returnsAsIs() {
    assertEquals("15714843763", PhoneUtil.normalizePhoneNumber("+1-571-484-3763"));
  }

  @Test
  void normalize_indiaInternationalFormat_stripsLeadingZeros() {
    // 091-9123456789 → strip non-digits → 0919123456789 → remove leading 0 → 919123456789
    assertEquals("919123456789", PhoneUtil.normalizePhoneNumber("091-9123456789"));
  }

  @Test
  void normalize_doublZeroPrefix_stripsLeadingZeros() {
    // 001-571-484-3763 → strip → 0015714843763 → remove leading 0s → 15714843763
    assertEquals("15714843763", PhoneUtil.normalizePhoneNumber("001-571-484-3763"));
  }

  @Test
  void normalize_lessThan10Digits_throwsValidationException() {
    assertThrows(ValidationException.class, () -> PhoneUtil.normalizePhoneNumber("12345"));
  }

  // --- formatPhoneNumber ---

  @Test
  void format_null_returnsNull() {
    assertNull(PhoneUtil.formatPhoneNumber(null));
  }

  @Test
  void format_blank_returnsNull() {
    assertNull(PhoneUtil.formatPhoneNumber("   "));
  }

  @Test
  void format_tenDigit_formatsWithIndiaCode() {
    assertEquals("+91-912 345 6789", PhoneUtil.formatPhoneNumber("9123456789"));
  }

  @Test
  void format_twelveDigitUsNumber_formatsWithCountryCode() {
    // 15714843763 → country code "1", local "5714843763"
    assertEquals("+1-571 484 3763", PhoneUtil.formatPhoneNumber("15714843763"));
  }

  @Test
  void format_twelveDigitIndiaNumber_formatsWithCountryCode() {
    assertEquals("+91-912 345 6789", PhoneUtil.formatPhoneNumber("919123456789"));
  }

  @Test
  void format_formattedInputWithPlusAndDashes_parsesCorrectly() {
    assertEquals("+1-571 484 3763", PhoneUtil.formatPhoneNumber("+1-571-484-3763"));
  }
}
