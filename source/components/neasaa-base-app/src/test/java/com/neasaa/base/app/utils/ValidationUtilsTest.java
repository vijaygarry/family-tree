package com.neasaa.base.app.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.neasaa.base.app.operation.exception.ValidationException;
import org.junit.jupiter.api.Test;

public class ValidationUtilsTest {

  @Test
  void checkValuePresent_throwsWhenNull() {
    assertThrows(ValidationException.class, () -> ValidationUtils.checkValuePresent(null, "field"));
  }

  @Test
  void checkValuePresent_throwsWhenEmpty() {
    assertThrows(ValidationException.class, () -> ValidationUtils.checkValuePresent("", "field"));
  }

  @Test
  void checkValuePresent_throwsWhenBlank() {
    assertThrows(ValidationException.class, () -> ValidationUtils.checkValuePresent("   ", "field"));
  }

  @Test
  void checkValuePresent_passesWhenValuePresent() {
    assertDoesNotThrow(() -> ValidationUtils.checkValuePresent("someValue", "field"));
  }

  @Test
  void checkValueRange_throwsBelowMin() {
    assertThrows(ValidationException.class, () -> ValidationUtils.checkValueRange(4, 5, 10, "age"));
  }

  @Test
  void checkValueRange_throwsAboveMax() {
    assertThrows(ValidationException.class, () -> ValidationUtils.checkValueRange(11, 5, 10, "age"));
  }

  @Test
  void checkValueRange_passesAtMin() {
    assertDoesNotThrow(() -> ValidationUtils.checkValueRange(5, 5, 10, "age"));
  }

  @Test
  void checkValueRange_passesAtMax() {
    assertDoesNotThrow(() -> ValidationUtils.checkValueRange(10, 5, 10, "age"));
  }

  @Test
  void checkValueRange_passesInRange() {
    assertDoesNotThrow(() -> ValidationUtils.checkValueRange(7, 5, 10, "age"));
  }

  @Test
  void checkObjectPresent_throwsWhenNull() {
    assertThrows(ValidationException.class, () -> ValidationUtils.checkObjectPresent(null, "obj"));
  }

  @Test
  void checkObjectPresent_passesWhenNonNull() {
    assertDoesNotThrow(() -> ValidationUtils.checkObjectPresent(new Object(), "obj"));
  }
}
