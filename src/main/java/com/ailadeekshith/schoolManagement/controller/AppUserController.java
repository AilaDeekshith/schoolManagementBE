package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.dto.ModulePermissionDTO;
import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.AppUser;
import com.ailadeekshith.schoolManagement.model.UserPermission;
import com.ailadeekshith.schoolManagement.repository.AppUserRepository;
import com.ailadeekshith.schoolManagement.repository.UserPermissionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppUserController {

    private final AppUserRepository userRepo;
    private final UserPermissionRepository permissionRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<AppUser>> getAll() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)));
    }

    @PostMapping
    public ResponseEntity<AppUser> create(@Valid @RequestBody AppUser user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + user.getEmail());
        }
        // Derive username from email if not provided
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            user.setUsername(user.getEmail().split("@")[0]);
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + user.getUsername());
        }
        // Default password: username@123
        String defaultPassword = user.getUsername() + "@123";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setPasswordChanged(false);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepo.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> update(@PathVariable Long id, @RequestBody AppUser updated) {
        AppUser user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());
        user.setUsername(updated.getUsername());
        user.setPhone(updated.getPhone());
        user.setDepartment(updated.getDepartment());
        user.setRole(updated.getRole());
        user.setStatus(updated.getStatus());
        user.setPhotoBase64(updated.getPhotoBase64());
        return ResponseEntity.ok(userRepo.save(user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppUser> updateStatus(@PathVariable Long id,
                                                @RequestParam AppUser.UserStatus status) {
        AppUser user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setStatus(status);
        return ResponseEntity.ok(userRepo.save(user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        permissionRepo.deleteByUserId(id);
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Module access (per-user permissions) ──────────────────
    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<ModulePermissionDTO>> getPermissions(@PathVariable Long id) {
        userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        List<ModulePermissionDTO> perms = permissionRepo.findByUserId(id).stream()
                .map(p -> new ModulePermissionDTO(p.getModule(), p.isCanRead(), p.isCanWrite()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(perms);
    }

    @PutMapping("/{id}/permissions")
    @Transactional
    public ResponseEntity<List<ModulePermissionDTO>> setPermissions(@PathVariable Long id,
                                                                    @RequestBody List<ModulePermissionDTO> perms) {
        AppUser user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        permissionRepo.deleteByUserId(id);
        if (perms != null) {
            perms.stream()
                    .filter(p -> p.getModule() != null && (p.isCanRead() || p.isCanWrite()))
                    .forEach(p -> permissionRepo.save(UserPermission.builder()
                            .user(user)
                            .module(p.getModule())
                            // Write always implies read.
                            .canRead(p.isCanRead() || p.isCanWrite())
                            .canWrite(p.isCanWrite())
                            .build()));
        }
        return getPermissions(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppUser>> search(@RequestParam String name) {
        return ResponseEntity.ok(userRepo.findByNameContainingIgnoreCase(name));
    }
}
