package com.EmpTimeHub.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for sending emails within the system.
 */
public interface MailService {

    /**
     * Sends an email from a sender to a recipient with the specified subject and body.
     *
     * @param from    Sender's email address.
     * @param to      Recipient's email address.
     * @param subject Subject of the email.
     * @param body    Body content of the email.
     * @param name    Name of the sender (used in the email body or signature).
     */
    void sendMail(String from, String to, String subject, String body, String name);
    void sendMail(String to, String body);
}
