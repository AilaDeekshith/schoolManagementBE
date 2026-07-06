package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.model.StudentUser;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.repository.StudentUserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only endpoint (protected by the existing admin security chain).
 * Allows admins to create and manage student app login accounts.
 * Default password for a new account is: username@123
 */
@RestController
@RequestMapping("/api/student-users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentUserManagementController {

    private final StudentUserRepository studentUserRepo;
    private final StudentRepository studentRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<StudentUser>> getAll() {
        return ResponseEntity.ok(studentUserRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentUser> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentUserRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + id)));
    }

    @PostMapping
    public ResponseEntity<StudentUser> create(@RequestBody CreateStudentUserRequest req) {
        if (studentUserRepo.existsByUsername(req.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + req.getUsername());
        }
        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + req.getStudentId()));

        String rawPassword = (req.getPassword() != null && !req.getPassword().isBlank())
                ? req.getPassword()
                : req.getUsername() + "@123";

        StudentUser studentUser = StudentUser.builder()
                .student(student)
                .username(req.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .passwordChanged(false)
                .status(StudentUser.Status.ACTIVE)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(studentUserRepo.save(studentUser));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StudentUser> updateStatus(@PathVariable Long id,
                                                    @RequestParam StudentUser.Status status) {
        StudentUser su = studentUserRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + id));
        su.setStatus(status);
        return ResponseEntity.ok(studentUserRepo.save(su));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentUserRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + id));
        studentUserRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class CreateStudentUserRequest {
        private Long studentId;
        private String username;
        private String password;
    }
}
