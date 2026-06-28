package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.AppUser;
import com.ailadeekshith.schoolManagement.repository.AppUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppUserController {

    private final AppUserRepository userRepo;

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
        if (user.getUsername() != null && userRepo.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + user.getUsername());
        }
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppUser>> search(@RequestParam String name) {
        return ResponseEntity.ok(userRepo.findByNameContainingIgnoreCase(name));
    }
}
