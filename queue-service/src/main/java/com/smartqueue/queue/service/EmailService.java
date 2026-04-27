package com.smartqueue.queue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ================= SEND EMAIL =================

    @Async // 🔥 Non-blocking
    public void sendEmail(String to, String subject, String text) {

        // 🔥 BASIC VALIDATION
        if (to == null || to.isBlank()) {
            log.warn("Email not sent: recipient is null/blank");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject != null ? subject : "SmartQueue Notification");
            message.setText(text != null ? text : "");

            mailSender.send(message);

            log.info("Email sent successfully to {}", to);

        } catch (Exception e) {
            log.error("Email sending failed for {} : {}", to, e.getMessage());
        }
    }
}