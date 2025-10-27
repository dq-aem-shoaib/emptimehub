package com.EmpTimeHub.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for sending emails within the system.
 */
public interface MailService {

    /**
     * Sends an email from a sender to a recipient with the specified subject and body.
     * @param to      Recipient's email address.
     * @param subject Subject of the email.
     * @param body    Body content of the email.
     *
     */
    void sendMail( String to, String subject, String body, MultipartFile multipartFile);
    void sendMail(String to, String body);
}
