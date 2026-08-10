package com.ailadeekshith.schoolManagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * Builds the AWS SES client from {@link EmailProperties}. The client bean is only
 * created when {@code app.mail.enabled=true}, so a misconfigured or disabled setup
 * simply results in no bean (emails are then logged & skipped by the service).
 * Credentials and region are read from application.properties — never hard-coded.
 */
@Configuration
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class SesConfig {

    private final EmailProperties props;

    @Bean
    @ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true")
    public SesV2Client sesV2Client() {
        EmailProperties.Aws aws = props.getAws();
        log.info("Initializing AWS SES client (region={})", aws.getRegion());
        return SesV2Client.builder()
                .region(Region.of(aws.getRegion()))
                .httpClient(UrlConnectionHttpClient.create())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(aws.getAccessKeyId(), aws.getSecretAccessKey())))
                .build();
    }
}
