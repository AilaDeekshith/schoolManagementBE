package com.ailadeekshith.schoolManagement.config;

import com.ailadeekshith.schoolManagement.repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StudentSecurityConfig {

    private final JwtUtil jwtUtil;
    private final StudentUserRepository studentUserRepo;

    @Bean
    @Order(1)
    public SecurityFilterChain studentFilterChain(HttpSecurity http) throws Exception {
        StudentJwtAuthFilter studentFilter = new StudentJwtAuthFilter(jwtUtil, studentUserRepo);

        return http
                .securityMatcher("/api/student/**")
                .cors(cors -> cors.configurationSource(studentCorsSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/student/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(studentFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean(name = "studentCorsSource")
    public CorsConfigurationSource studentCorsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
