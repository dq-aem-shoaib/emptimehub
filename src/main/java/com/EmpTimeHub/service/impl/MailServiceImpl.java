package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.service.MailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Implementation of MailService for sending emails using JavaMailSender.
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String defaultFrom;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email from a sender to a recipient with the specified subject and body.
     *
     * @param from    Sender's email address.
     * @param to      Recipient's email address.
     * @param subject Subject of the email.
     * @param body    Body content of the email.
     */
    @Override
    public void sendMail(String from, String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom(from);      // plain sender email address
            helper.setTo(to);          // recipient
            helper.setSubject(subject);
            helper.setText(body, false); // false = plain text (not HTML)

            mailSender.send(message);

            log.info("Email sent successfully from '{}' to '{}', subject='{}'", from, to, subject);
        } catch (MessagingException  e) {
            log.error("Failed to send email from '{}' to '{}', subject='{}': {}", from, to, subject, e.getMessage(), e);
        }
    }

}
