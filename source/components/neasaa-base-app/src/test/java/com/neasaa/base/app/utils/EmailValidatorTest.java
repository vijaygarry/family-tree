package com.neasaa.base.app.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.neasaa.base.app.operation.exception.ValidationException;
import org.junit.jupiter.api.Test;

public class EmailValidatorTest {

  // --- validateEmail: mandatory ---

  @Test
  void validateEmail_mandatory_nullThrows() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail(null, true));
  }

  @Test
  void validateEmail_mandatory_blankThrows() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("  ", true));
  }

  @Test
  void validateEmail_mandatory_validEmailPasses() {
    assertDoesNotThrow(() -> EmailValidator.validateEmail("user@example.com", true));
  }

  // --- validateEmail: optional ---

  @Test
  void validateEmail_optional_nullPasses() {
    assertDoesNotThrow(() -> EmailValidator.validateEmail(null, false));
  }

  @Test
  void validateEmail_optional_blankPasses() {
    assertDoesNotThrow(() -> EmailValidator.validateEmail("", false));
  }

  // --- validateEmail: length checks ---

  @Test
  void validateEmail_over254Chars_throws() {
    String longEmail = "a".repeat(245) + "@b.com"; // 252 chars local = exceeds 254 total
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail(longEmail, true));
  }

  // --- validateEmail: @ checks ---

  @Test
  void validateEmail_noAt_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("userexample.com", true));
  }

  @Test
  void validateEmail_multipleAt_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user@@example.com", true));
  }

  // --- validateEmail: local part checks ---

  @Test
  void validateEmail_emptyLocalPart_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("@example.com", true));
  }

  @Test
  void validateEmail_localPartOver64Chars_throws() {
    String localPart = "a".repeat(65);
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail(localPart + "@example.com", true));
  }

  @Test
  void validateEmail_localPartInvalidChars_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user name@example.com", true));
  }

  // --- validateEmail: domain part checks ---

  @Test
  void validateEmail_emptyDomainPart_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user@", true));
  }

  @Test
  void validateEmail_domainPartOver64Chars_throws() {
    String domain = "a".repeat(65) + ".com";
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user@" + domain, true));
  }

  @Test
  void validateEmail_domainInvalidChars_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user@exam_ple.com", true));
  }

  @Test
  void validateEmail_domainStartsWithHyphen_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user@-example.com", true));
  }

  @Test
  void validateEmail_domainEndsWithHyphen_throws() {
    // Domain part "example-" ends with a hyphen (no TLD to make it unambiguous)
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user@example-", true));
  }

  @Test
  void validateEmail_domainNoDot_throws() {
    assertThrows(ValidationException.class, () -> EmailValidator.validateEmail("user@examplecom", true));
  }

  // --- isEmailId ---

  @Test
  void isEmailId_validEmail_returnsTrue() {
    assertTrue(EmailValidator.isEmailId("user@example.com"));
  }

  @Test
  void isEmailId_invalidEmail_returnsFalse() {
    assertFalse(EmailValidator.isEmailId("not-an-email"));
  }

  @Test
  void isEmailId_null_returnsFalse() {
    assertFalse(EmailValidator.isEmailId(null));
  }

  @Test
  void isEmailId_empty_returnsFalse() {
    assertFalse(EmailValidator.isEmailId(""));
  }

  @Test
  void isEmailId_emailWithSubdomain_returnsTrue() {
    assertTrue(EmailValidator.isEmailId("user@mail.example.co.in"));
  }
}
