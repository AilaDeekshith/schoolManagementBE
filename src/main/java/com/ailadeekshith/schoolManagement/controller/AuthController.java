package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.config.JwtUtil;
import com.ailadeekshith.schoolManagement.dto.ModulePermissionDTO;
import com.ailadeekshith.schoolManagement.model.AppUser;
import com.ailadeekshith.schoolManagement.repository.AppUserRepository;
import com.ailadeekshith.schoolManagement.repository.UserPermissionRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AppUserRepository userRepo;
    private final UserPermissionRepository permissionRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }

        AppUser user = userRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        List<ModulePermissionDTO> permissions = permissionRepo.findByUserId(user.getId()).stream()
                .map(p -> new ModulePermissionDTO(p.getModule(), p.isCanRead(), p.isCanWrite()))
                .collect(Collectors.toList());

        Map<String, Object> body = new HashMap<>();
        body.put("token",           token);
        body.put("username",        user.getUsername());
        body.put("name",            user.getName());
        body.put("role",            user.getRole().name());
        body.put("passwordChanged", user.isPasswordChanged());
        body.put("permissions",     permissions);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req,
                                            Authentication auth) {
        AppUser user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Current password is incorrect"));
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setPasswordChanged(true);
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }
}
