package com.neasaa.base.app.utils.email;

import com.neasaa.base.app.operation.exception.InternalServerException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


import java.io.File;
import java.util.Map;

import static com.neasaa.base.app.operation.BeanNames.APP_EMAIL_SENDER;
import static com.neasaa.base.app.operation.BeanNames.EMAIL_SENDER_TEMPLATE_BEAN;

@Log4j2
@Component(APP_EMAIL_SENDER)
public class EmailSender {

    @Autowired
    @Qualifier(EMAIL_SENDER_TEMPLATE_BEAN)
    private JavaMailSender springEmailSender;


    public void sendEmail(EmailMessage emailMessage) {
        // TODO: Improve email sending using website: https://www.mail-tester.com/
        MimeMessage mimeMessage = springEmailSender.createMimeMessage();

        try {
            // true = multipart
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            if (emailMessage.getFromDisplayName() == null || emailMessage.getFromDisplayName().isEmpty()) {
                helper.setFrom(emailMessage.getFrom());
            } else {
                helper.setFrom(new InternetAddress(emailMessage.getFrom(), emailMessage.getFromDisplayName()));
            }
            if (emailMessage.getReplyTo() != null && !emailMessage.getReplyTo().isEmpty()) {
                //TODO: Set the reply to display name
                helper.setReplyTo(new InternetAddress(emailMessage.getReplyTo()));
            }
            // Recipients
            if (emailMessage.getTo() != null) {
                helper.setTo(emailMessage.getTo().toArray(new String[0]));
            }
            if (emailMessage.getCc() != null) {
                helper.setCc(emailMessage.getCc().toArray(new String[0]));
            }
            if (emailMessage.getBcc() != null) {
                helper.setBcc(emailMessage.getBcc().toArray(new String[0]));
            }

            // Subject & Body
            helper.setSubject(emailMessage.getSubject());
            boolean isHtml = emailMessage.getType() == EmailMessage.EmailType.HTML;
            helper.setText(emailMessage.getBody(), isHtml);

            // Attachments
            if (emailMessage.getAttachments() != null) {
                for (Map.Entry<String, String> entry : emailMessage.getAttachments().entrySet()) {
                    FileSystemResource file = new FileSystemResource(new File(entry.getValue()));
                    helper.addAttachment(entry.getKey(), file);
                }
            }

            //Inline resources (images, etc.)
            if (emailMessage.getInlineResources() != null) {
                for (Map.Entry<String, String> entry : emailMessage.getInlineResources().entrySet()) {
                    FileSystemResource res = new FileSystemResource(new File(entry.getValue()));
                    helper.addInline(entry.getKey(), res);
                }
            }

            springEmailSender.send(mimeMessage);

        } catch (Exception e) {
            log.info("Failed to create mime message helper", e);
            throw new InternalServerException("Failed to send email", e);
        }
        log.info("Email sent successfully to {}", emailMessage.getTo());
    }
}
