package com.neasaa.base.app.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

public class DBEncryptionUtilTest {

  @Test
  void decrypt_withoutEnvVar_throwsRuntimeException() {
    // DB_PASSWORD_ENCRYPTION_SALT_KEY must NOT be set for this test to be meaningful.
    // If the env var is set in your CI/local environment, this test will be skipped via the paired test below.
    String saltKey = System.getenv("DB_PASSWORD_ENCRYPTION_SALT_KEY");
    if (saltKey != null) {
      return; // env var is set — skip this negative test
    }
    assertThrows(RuntimeException.class, () -> DBEncryptionUtil.decrypt("anyEncryptedValue"));
  }

  // Round-trip test: only runs when DB_PASSWORD_ENCRYPTION_SALT_KEY is set in the environment.
  // To run locally: export DB_PASSWORD_ENCRYPTION_SALT_KEY=testkey && ./gradlew :components:neasaa-base-app:test
  @Test
  @EnabledIfEnvironmentVariable(named = "DB_PASSWORD_ENCRYPTION_SALT_KEY", matches = ".+")
  void decrypt_withEnvVar_roundTripsCorrectly() throws Exception {
    // Use reflection to call the private encrypt method for the round-trip.
    var encryptMethod = DBEncryptionUtil.class.getDeclaredMethod("encrypt", String.class);
    encryptMethod.setAccessible(true);
    String plainText = "testPassword";
    String encrypted = (String) encryptMethod.invoke(null, plainText);
    String decrypted = DBEncryptionUtil.decrypt(encrypted);
    assertEquals(plainText, decrypted);
  }
}
