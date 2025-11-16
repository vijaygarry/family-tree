package com.neasaa.base.app.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
// While running the app, specify the environment variable app.properties.file.name to point to the
// properties file name
@PropertySource("classpath:${app.properties.file.name}")
public class AppProperties {

  // Email configurations
  @Value("${email.server.host}")
  private String emailServerHost;

  @Value("${email.server.port}")
  private int emailServerPort;

  @Value("${email.server.username}")
  private String emailServerUsername;

  @Value("${email.server.password}")
  private String emailServerPassword;

  @Value("${email.server.smtp.auth}")
  private boolean emailServerSmtpAuth;

  @Value("${email.server.smtp.starttls.enable}")
  private boolean emailServerSmtpStarttlsEnable;

  @Value("${email.sender.emailid}")
  private String emailSenderEmailId;

  @Value("${email.sender.displayname}")
  private String emailSenderDisplayName;

  @Value("${email.list.for.sms.otp}")
  private String emailListForSMSOtp;

  @Value("${app.upload.dir}")
  private String appUploadDir;
}
