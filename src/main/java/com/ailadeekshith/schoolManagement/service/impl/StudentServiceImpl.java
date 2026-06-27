package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Student createStudent(Student student) {
        log.info("Creating student: {}", student.getName());
        if (student.getEmail() != null && studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + student.getEmail());
        }
        return studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id);
        existing.setName(updated.getName());
        existing.setDob(updated.getDob());
        existing.setGender(updated.getGender());
        existing.setBloodGroup(updated.getBloodGroup());
        existing.setAddress(updated.getAddress());
        existing.setEmail(updated.getEmail());
        existing.setClassName(updated.getClassName());
        existing.setRollNumber(updated.getRollNumber());
        existing.setGuardianName(updated.getGuardianName());
        existing.setContactNumber(updated.getContactNumber());
        existing.setStatus(updated.getStatus());
        existing.setFeeStatus(updated.getFeeStatus());
        existing.setBusRoute(updated.getBusRoute());
        existing.setMedicalNotes(updated.getMedicalNotes());
        log.info("Updated student id: {}", id);
        return studentRepository.save(existing);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
        log.info("Deleted student id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getStudentsByStatus(Student.StudentStatus status) {
        return studentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getStudentsByFeeStatus(Student.FeeStatus feeStatus) {
        return studentRepository.findByFeeStatus(feeStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveStudents() {
        return studentRepository.countActiveStudents();
    }
}