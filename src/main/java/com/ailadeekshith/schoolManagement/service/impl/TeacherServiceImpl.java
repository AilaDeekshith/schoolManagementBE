package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.repository.TeacherRepository;
import com.ailadeekshith.schoolManagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    @Override
    public Teacher createTeacher(Teacher teacher) {
        log.info("Creating teacher: {}", teacher.getName());
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + teacher.getEmail());
        }
        return teacherRepository.save(teacher);
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