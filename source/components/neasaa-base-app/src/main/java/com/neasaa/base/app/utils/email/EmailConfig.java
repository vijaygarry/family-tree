package com.neasaa.base.app.utils.email;

import com.neasaa.base.app.operation.BeanNames;
import com.neasaa.base.app.utils.AppProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

@Log4j2
@Configuration
public class EmailConfig {

    @Autowired
    private AppProperties appProperties;

    @Bean(name = BeanNames.EMAIL_SENDER_TEMPLATE_BEAN)
    public JavaMailSender springEmailSender() throws IOException {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(appProperties.getEmailServerHost());
        mailSender.setPort(appProperties.getEmailServerPort());
        mailSender.setUsername(appProperties.getEmailServerUsername());
        //TODO: decrypt password
        mailSender.setPassword(appProperties.getEmailServerPassword());

        Properties props = new Properties();
        props.put("mail.smtp.auth", appProperties.isEmailServerSmtpAuth());
        props.put("mail.smtp.starttls.enable", appProperties.isEmailServerSmtpStarttlsEnable());

        log.info("Initialized email sender with host: {}, port: {}, username: {}",
                appProperties.getEmailServerHost(), appProperties.getEmailServerPort(),
                appProperties.getEmailServerUsername());
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }

}
