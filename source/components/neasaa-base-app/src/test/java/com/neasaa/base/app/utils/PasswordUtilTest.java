package com.neasaa.base.app.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PasswordUtilTest {

  @Test
  void hashPassword_returnsNonNull() {
    assertNotNull(PasswordUtil.hashPassword("secret123"));
  }

  @Test
  void hashPassword_doesNotReturnPlainText() {
    String hash = PasswordUtil.hashPassword("secret123");
    assertNotEquals("secret123", hash);
  }

  @Test
  void hashPassword_producesDifferentHashEachTime() {
    // Argon2 uses a random salt per hash
    String hash1 = PasswordUtil.hashPassword("secret123");
    String hash2 = PasswordUtil.hashPassword("secret123");
    assertNotEquals(hash1, hash2);
  }

  @Test
  void matchPassword_trueForCorrectPassword() {
    String hash = PasswordUtil.hashPassword("myPassword");
    assertTrue(PasswordUtil.matchPassword("myPassword", hash));
  }

  @Test
  void matchPassword_falseForWrongPassword() {
    String hash = PasswordUtil.hashPassword("myPassword");
    assertFalse(PasswordUtil.matchPassword("wrongPassword", hash));
  }

  @Test
  void matchPassword_falseForEmptyPassword() {
    String hash = PasswordUtil.hashPassword("myPassword");
    assertFalse(PasswordUtil.matchPassword("", hash));
  }
}
