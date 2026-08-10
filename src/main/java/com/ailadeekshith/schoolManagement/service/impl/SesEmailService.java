package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.config.EmailProperties;
import com.ailadeekshith.schoolManagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

/**
 * AWS SES implementation of {@link EmailService}. Runs on a background thread and
 * never throws — failures are logged so the calling transaction is unaffected.
 * The SES client bean only exists when {@code app.mail.enabled=true}; otherwise
 * sends are logged and skipped.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SesEmailService implements EmailService {

    private final EmailProperties props;
    private final ObjectProvider<SesV2Client> sesClientProvider;

    @Async
    @Override
    public void sendEmail(String to, String subject, String htmlBody, String textBody) {
        if (!props.isEnabled()) {
            log.debug("Email disabled (app.mail.enabled=false); skipping send to {}", to);
            return;
        }
        if (to == null || !to.contains("@")) {
            log.warn("Skipping email — invalid recipient address: {}", to);
            return;
        }
        SesV2Client client = sesClientProvider.getIfAvailable();
        if (client == null) {
            log.warn("SES client unavailable; skipping email to {}", to);
            return;
        }

        try {
            String source = (props.getFromName() != null && !props.getFromName().isBlank())
                    ? props.getFromName() + " <" + props.getFrom() + ">"
                    : props.getFrom();

            Body.Builder body = Body.builder()
                    .html(Content.builder().data(htmlBody).charset("UTF-8").build());
            if (textBody != null && !textBody.isBlank()) {
                body.text(Content.builder().data(textBody).charset("UTF-8").build());
            }

            SendEmailRequest request = SendEmailRequest.builder()
                    .fromEmailAddress(source)
                    .destination(Destination.builder().toAddresses(to).build())
                    .content(EmailContent.builder()
                            .simple(Message.builder()
                                    .subject(Content.builder().data(subject).charset("UTF-8").build())
                                    .body(body.build())
                                    .build())
                            .build())
                    .build();

            client.sendEmail(request);
            log.info("Email sent to {} (subject: {})", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
