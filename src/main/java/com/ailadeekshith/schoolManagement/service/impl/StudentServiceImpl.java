package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Fees;
import com.ailadeekshith.schoolManagement.model.FeeStructure;
import com.ailadeekshith.schoolManagement.model.SchoolProfile;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.repository.FeesRepository;
import com.ailadeekshith.schoolManagement.repository.FeeStructureRepository;
import com.ailadeekshith.schoolManagement.repository.SchoolProfileRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final FeesRepository feesRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final SchoolProfileRepository schoolProfileRepository;

    @Override
    public Student createStudent(Student student) {
        log.info("Creating student: {}", student.getName());
        if (student.getEmail() != null && studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + student.getEmail());
        }
        Student saved = studentRepository.save(student);

        BigDecimal totalFee = feeStructureRepository.findAll().stream()
                .filter(fs -> !Boolean.FALSE.equals(fs.getIsActive()))
                .filter(fs -> matchesClass(fs.getGradeName(), saved.getClassName()))
                .map(fs -> fs.getAmount() != null ? fs.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String academicYear = schoolProfileRepository.findById(1L)
                .map(SchoolProfile::getAcademicYear)
                .filter(y -> y != null && !y.isBlank())
                .orElseGet(this::computeCurrentAcademicYear);

        feesRepository.save(Fees.builder()
                .student(saved)
                .totalAmount(totalFee)
                .paidAmount(BigDecimal.ZERO)
                .feeStatus(Fees.FeeStatus.PENDING)
                .feeType(Fees.FeeType.TUITION)
                .academicYear(academicYear)
                .build());
        return saved;
    }

    /** Returns e.g. "2025-26" for April–Dec 2025 or "2024-25" for Jan–Mar 2025. */
    private String computeCurrentAcademicYear() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        if (today.getMonthValue() >= 4) {
            return year + "-" + String.valueOf(year + 1).substring(2);
        } else {
            return (year - 1) + "-" + String.valueOf(year).substring(2);
        }
    }

    private boolean matchesClass(String gradeName, String className) {
        if (gradeName == null || className == null) return false;
        if ("all".equalsIgnoreCase(gradeName.trim())) return true;
        if (className.equalsIgnoreCase(gradeName.trim())) return true;
        return className.toLowerCase().startsWith(gradeName.toLowerCase().trim() + "-");
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