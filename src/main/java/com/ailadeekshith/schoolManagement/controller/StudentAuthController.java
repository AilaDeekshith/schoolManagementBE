package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.config.JwtUtil;
import com.ailadeekshith.schoolManagement.model.StudentUser;
import com.ailadeekshith.schoolManagement.repository.StudentUserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentAuthController {

    private final StudentUserRepository studentUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        StudentUser studentUser = studentUserRepo.findByUsername(req.getUsername()).orElse(null);

        if (studentUser == null || !passwordEncoder.matches(req.getPassword(), studentUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }

        if (studentUser.getStatus() != StudentUser.Status.ACTIVE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Account is inactive. Contact your school administrator."));
        }

        String token = jwtUtil.generateToken(studentUser.getUsername(), "STUDENT");

        return ResponseEntity.ok(Map.of(
                "token",           token,
                "username",        studentUser.getUsername(),
                "name",            studentUser.getStudent().getName(),
                "studentId",       studentUser.getStudent().getId(),
                "className",       studentUser.getStudent().getClassName(),
                "passwordChanged", studentUser.isPasswordChanged()
        ));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
