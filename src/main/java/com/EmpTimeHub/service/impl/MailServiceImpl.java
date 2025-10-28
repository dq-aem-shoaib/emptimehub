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
import org.springframework.web.multipart.MultipartFile;

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
     * @param to      Recipient's email address.
     * @param subject Subject of the email.
     * @param body    Body content of the email.
     */
    @Override
    public void sendMail(String to, String subject, String body, MultipartFile attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(defaultFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body.replace("\n", "<br/>"), true);
            if (attachment != null && !attachment.isEmpty()) {
                helper.addAttachment(attachment.getOriginalFilename(), attachment);
                log.info("Attached file '{}' to email.", attachment.getOriginalFilename());
            }

            mailSender.send(message);

            log.info("Email sent successfully  to '{}', subject='{}'", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email  to '{}', subject='{}': {}", to, subject, e.getMessage(), e);
        }
    }

    @Override
    public void sendMail(String to, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(defaultFrom);
            helper.setTo(to);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("Email sent successfully to '{}', body='{}'", to, body);
        } catch (MessagingException e) {
            log.error("Failed to send email to '{}': {}", to, e.getMessage());
        }
    }
}
