package com.ailadeekshith.schoolManagement.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Prevents JwtAuthFilter from being registered as a standalone Servlet filter.
 * It is already registered inside the admin SecurityFilterChain via addFilterBefore().
 * Without this, it would also run as a global filter and break student-route requests
 * by looking up student usernames in the admin (app_users) table.
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
