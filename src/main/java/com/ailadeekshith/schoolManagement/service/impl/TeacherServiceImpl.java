package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.AppUser;
import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.repository.AppUserRepository;
import com.ailadeekshith.schoolManagement.repository.TeacherRepository;
import com.ailadeekshith.schoolManagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Teacher createTeacher(Teacher teacher) {
        log.info("Creating teacher: {}", teacher.getName());
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + teacher.getEmail());
        }
        Teacher saved = teacherRepository.save(teacher);

        // Auto-create an app login (role TEACHER) with a default password.
        createLoginForTeacher(saved);
        return saved;
    }

    /** Creates a staff login for a teacher (username + default password username@123). */
    private void createLoginForTeacher(Teacher teacher) {
        String email = (teacher.getEmail() != null && teacher.getEmail().contains("@")) ? teacher.getEmail() : null;
        String base = email != null ? sanitize(email.substring(0, email.indexOf('@'))) : sanitize(teacher.getName());
        String username = uniqueUsername(base);
        String userEmail = email != null ? email : username + "@school.local";
        if (appUserRepository.existsByEmail(userEmail)) {
            log.warn("Skipped teacher login creation — email already in use: {}", userEmail);
            return;
        }
        String rawPassword = username + "@123";
        appUserRepository.save(AppUser.builder()
                .name(teacher.getName())
                .email(userEmail)
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .passwordChanged(false)
                .role(AppUser.UserRole.TEACHER)
                .status(AppUser.UserStatus.ACTIVE)
                .build());
        log.info("Created teacher login '{}' (default password {}@123)", username, username);
    }

    private String sanitize(String s) {
        if (s == null) return "teacher";
        String out = s.toLowerCase().replaceAll("[^a-z0-9]", "");
        return out.isBlank() ? "teacher" : out;
    }

    private String uniqueUsername(String base) {
        String username = base;
        int i = 0;
        while (appUserRepository.existsByUsername(username)) {
            i++;
            username = base + i;
        }
        return username;
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher updateTeacher(Long id, Teacher updated) {
        Teacher existing = getTeacherById(id);
        existing.setName(updated.getName());
        existing.setSubject(updated.getSubject());
        existing.setEmail(updated.getEmail());
        existing.setContactNumber(updated.getContactNumber());
        existing.setQualification(updated.getQualification());
        existing.setExperience(updated.getExperience());
        existing.setAssignedClasses(updated.getAssignedClasses());
        existing.setStatus(updated.getStatus());
        existing.setPhotoBase64(updated.getPhotoBase64());
        log.info("Updated teacher id: {}", id);
        return teacherRepository.save(existing);
    }

    @Override
    public void deleteTeacher(Long id) {
        Teacher teacher = getTeacherById(id);
        teacherRepository.delete(teacher);
        log.info("Deleted teacher id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> getTeachersBySubject(String subject) {
        return teacherRepository.findBySubject(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> getTeachersByStatus(Teacher.TeacherStatus status) {
        return teacherRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> searchTeachersByName(String name) {
        return teacherRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> getTeachersByClass(String className) {
        return teacherRepository.findByAssignedClassesContaining(className);
    }
}