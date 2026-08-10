package com.ailadeekshith.schoolManagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds all email/AWS-SES settings from application.properties (prefix {@code app.mail}).
 * No values are hard-coded here — everything comes from configuration.
 */
@Component
@ConfigurationProperties(prefix = "app.mail")
@Data
public class EmailProperties {

    /** Master switch. When false the app never contacts SES. */
    private boolean enabled = false;

    /** Verified SES sender address (e.g. no-reply@yourschool.com). */
    private String from;

    /** Friendly display name shown to recipients. */
    private String fromName = "School Management";

    /** Portal URL included in notification emails (login button). May be blank. */
    private String loginUrl;

    /** AWS SES specific settings. */
    private final Aws aws = new Aws();

    @Data
    public static class Aws {
        /** AWS region of the SES identity, e.g. us-east-1. */
        private String region;
        /** IAM access key id with ses:SendEmail permission. */
        private String accessKeyId;
        /** IAM secret access key. */
        private String secretAccessKey;
    }
}
