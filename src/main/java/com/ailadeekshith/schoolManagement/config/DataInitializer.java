package com.ailadeekshith.schoolManagement.config;

import com.ailadeekshith.schoolManagement.model.AppUser;
import com.ailadeekshith.schoolManagement.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepo.count() == 0) {
            AppUser admin = AppUser.builder()
                    .name("Super Admin")
                    .email("admin@school.in")
                    .username("admin")
                    .password(passwordEncoder.encode("Admin@123"))
                    .passwordChanged(true)
                    .role(AppUser.UserRole.SUPER_ADMIN)
                    .status(AppUser.UserStatus.ACTIVE)
                    .build();
            userRepo.save(admin);
            log.info("Default admin created — username: admin  password: Admin@123");
        }
    }
}
