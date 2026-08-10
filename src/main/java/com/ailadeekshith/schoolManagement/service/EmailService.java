package com.ailadeekshith.schoolManagement.service;

/**
 * Sends transactional email notifications. Implementations are expected to fail
 * softly — a delivery problem must never break the business operation that
 * triggered the notification.
 */
public interface EmailService {

    /**
     * Sends an email asynchronously.
     *
     * @param to       recipient address
     * @param subject  subject line
     * @param htmlBody HTML body
     * @param textBody plain-text fallback (may be null)
     */
    void sendEmail(String to, String subject, String htmlBody, String textBody);
}
