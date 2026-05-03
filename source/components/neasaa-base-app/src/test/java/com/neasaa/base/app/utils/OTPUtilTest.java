package com.neasaa.base.app.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.neasaa.base.app.entity.OtpVerification;
import com.neasaa.base.app.enums.OTPStatus;
import com.neasaa.base.app.enums.OTPType;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.utils.email.EmailSender;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OTPUtilTest {

  @Mock
  private AppProperties appProperties;

  @Mock
  private EmailSender emailSender;

  // --- generateOTP ---

  @Test
  void generateOTP_returnsSixCharacterString() {
    String otp = OTPUtil.generateOTP();
    assertEquals(6, otp.length());
  }

  @Test
  void generateOTP_containsOnlyDigits() {
    String otp = OTPUtil.generateOTP();
    assertTrue(otp.matches("\\d{6}"), "OTP should contain only digits: " + otp);
  }

  // --- generateRequestId ---

  @Test
  void generateRequestId_returnsNonNull() {
    assertNotNull(OTPUtil.generateRequestId());
  }

  @Test
  void generateRequestId_isValidUUID() {
    String requestId = OTPUtil.generateRequestId();
    assertDoesNotThrow(() -> UUID.fromString(requestId));
  }

  // --- isOtpExpired ---

  @Test
  void isOtpExpired_expiredByTime_returnsTrue() {
    OtpVerification otp = OtpVerification.builder()
        .status(OTPStatus.Pending)
        .expiryDate(new Date(System.currentTimeMillis() - 1000)) // 1 second in the past
        .build();
    assertTrue(OTPUtil.isOtpExpired(otp));
  }

  @Test
  void isOtpExpired_nonPendingStatus_returnsTrue() {
    OtpVerification otp = OtpVerification.builder()
        .status(OTPStatus.Verified)
        .expiryDate(new Date(System.currentTimeMillis() + 60_000))
        .build();
    assertTrue(OTPUtil.isOtpExpired(otp));
  }

  @Test
  void isOtpExpired_validOtp_returnsFalse() {
    OtpVerification otp = OtpVerification.builder()
        .status(OTPStatus.Pending)
        .expiryDate(new Date(System.currentTimeMillis() + 60_000)) // 1 minute in the future
        .build();
    assertFalse(OTPUtil.isOtpExpired(otp));
  }

  @Test
  void isOtpExpired_nullExpiryDate_returnsFalse() {
    OtpVerification otp = OtpVerification.builder()
        .status(OTPStatus.Pending)
        .expiryDate(null)
        .build();
    assertFalse(OTPUtil.isOtpExpired(otp));
  }

  // --- getLogonNameSuggestion ---

  @Test
  void getLogonNameSuggestion_extractsLocalPartOfEmail() {
    assertEquals("vijay", OTPUtil.getLogonNameSuggestion("vijay@example.com"));
  }

  // --- isOTPValid ---

  @Test
  void isOTPValid_correctOtp_returnsTrue() throws Exception {
    String rawOtp = "123456";
    String hashedOtp = PasswordUtil.hashPassword(rawOtp);
    OtpVerification otp = OtpVerification.builder().hashOtpCode(hashedOtp).build();
    assertTrue(OTPUtil.isOTPValid(rawOtp, otp));
  }

  @Test
  void isOTPValid_wrongOtp_returnsFalse() throws Exception {
    String hashedOtp = PasswordUtil.hashPassword("123456");
    OtpVerification otp = OtpVerification.builder().hashOtpCode(hashedOtp).build();
    assertFalse(OTPUtil.isOTPValid("999999", otp));
  }

  // --- buildWhatsAppUrl ---

  @Test
  void buildWhatsAppUrl_nullPhone_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> OTPUtil.buildWhatsAppUrl(null, "hello"));
  }

  @Test
  void buildWhatsAppUrl_emptyPhone_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> OTPUtil.buildWhatsAppUrl("", "hello"));
  }

  @Test
  void buildWhatsAppUrl_validInput_containsPhoneAndEncodedMessage() {
    String url = OTPUtil.buildWhatsAppUrl("919123456789", "Hello World");
    assertTrue(url.startsWith("https://wa.me/919123456789?text="));
    assertTrue(url.contains("Hello+World") || url.contains("Hello%20World"));
  }

  @Test
  void buildWhatsAppUrl_nullMessage_returnsUrlWithEmptyText() {
    String url = OTPUtil.buildWhatsAppUrl("919123456789", null);
    assertTrue(url.startsWith("https://wa.me/919123456789?text="));
  }

  // --- sendOtpEmail ---

  @Test
  void sendOtpEmail_forgotPassword_sendsEmail() {
    when(appProperties.getEmailSenderEmailId()).thenReturn("sender@example.com");
    when(appProperties.getEmailSenderDisplayName()).thenReturn("Rajput Chhipa");
    doNothing().when(emailSender).sendEmail(any());

    assertDoesNotThrow(() -> OTPUtil.sendOtpEmail(
        "user@example.com", "123456", OTPType.FORGOT_PASSWORD,
        "Vijay", "Kumar", appProperties, emailSender));

    verify(emailSender, times(1)).sendEmail(any());
  }

  @Test
  void sendOtpEmail_signUp_sendsEmail() {
    when(appProperties.getEmailSenderEmailId()).thenReturn("sender@example.com");
    when(appProperties.getEmailSenderDisplayName()).thenReturn("Rajput Chhipa");
    doNothing().when(emailSender).sendEmail(any());

    assertDoesNotThrow(() -> OTPUtil.sendOtpEmail(
        "user@example.com", "654321", OTPType.SIGN_UP,
        "Vijay", "Kumar", appProperties, emailSender));

    verify(emailSender, times(1)).sendEmail(any());
  }

  @Test
  void sendOtpEmail_unsupportedOtpType_throwsInternalServerException() {
    assertThrows(InternalServerException.class, () -> OTPUtil.sendOtpEmail(
        "user@example.com", "123456", OTPType.DEVICE_AUTHENTICATION,
        "Vijay", "Kumar", appProperties, emailSender));
  }

  // --- sendOtpSMS ---

  @Test
  void sendOtpSMS_forgotPassword_sendsEmail() {
    when(appProperties.getEmailSenderEmailId()).thenReturn("sender@example.com");
    when(appProperties.getEmailSenderDisplayName()).thenReturn("Rajput Chhipa");
    when(appProperties.getEmailListForSMSOtp()).thenReturn("admin@example.com");
    doNothing().when(emailSender).sendEmail(any());

    assertDoesNotThrow(() -> OTPUtil.sendOtpSMS(
        "919123456789", "123456", OTPType.FORGOT_PASSWORD,
        "Vijay", "Kumar", appProperties, emailSender));

    verify(emailSender, times(1)).sendEmail(any());
  }

  @Test
  void sendOtpSMS_signUp_sendsEmail() {
    when(appProperties.getEmailSenderEmailId()).thenReturn("sender@example.com");
    when(appProperties.getEmailSenderDisplayName()).thenReturn("Rajput Chhipa");
    when(appProperties.getEmailListForSMSOtp()).thenReturn("admin@example.com");
    doNothing().when(emailSender).sendEmail(any());

    assertDoesNotThrow(() -> OTPUtil.sendOtpSMS(
        "919123456789", "654321", OTPType.SIGN_UP,
        "Vijay", "Kumar", appProperties, emailSender));

    verify(emailSender, times(1)).sendEmail(any());
  }

  @Test
  void sendOtpSMS_unsupportedOtpType_throwsInternalServerException() {
    assertThrows(InternalServerException.class, () -> OTPUtil.sendOtpSMS(
        "919123456789", "123456", OTPType.DEVICE_AUTHENTICATION,
        "Vijay", "Kumar", appProperties, emailSender));
  }
}
