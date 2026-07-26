package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Fees;
import com.ailadeekshith.schoolManagement.model.FeeStructure;
import com.ailadeekshith.schoolManagement.model.SchoolProfile;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.model.StudentUser;
import com.ailadeekshith.schoolManagement.repository.FeesRepository;
import com.ailadeekshith.schoolManagement.repository.FeeStructureRepository;
import com.ailadeekshith.schoolManagement.repository.SchoolProfileRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.repository.StudentUserRepository;
import com.ailadeekshith.schoolManagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private final StudentUserRepository studentUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Student createStudent(Student student) {
        log.info("Creating student: {}", student.getName());
        if (student.getEmail() != null && studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + student.getEmail());
        }
        // Generate the unique student identifier from the admission date + time.
        student.setStudentCode(generateStudentCode(student.getAdmissionDate()));
        Student saved = studentRepository.save(student);

        // Auto-create a student-portal login with a default password.
        createLoginForStudent(saved);

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

    /** Builds a unique identifier like 202607251204 (yyyyMMddHHmm) from the admission date + current time. */
    private String generateStudentCode(LocalDate admissionDate) {
        LocalDate ad = admissionDate != null ? admissionDate : LocalDate.now();
        LocalTime now = LocalTime.now();
        String base = String.format("%04d%02d%02d%02d%02d",
                ad.getYear(), ad.getMonthValue(), ad.getDayOfMonth(), now.getHour(), now.getMinute());
        String code = base;
        int suffix = 0;
        while (studentRepository.existsByStudentCode(code)) {
            suffix++;
            code = base + suffix; // avoid rare same-minute collisions
        }
        return code;
    }

    /** Creates a student-portal login (username + default password username@123). */
    private void createLoginForStudent(Student student) {
        String base = usernameBase(student.getEmail(), student.getName());
        String username = uniqueStudentUsername(base);
        String rawPassword = username + "@123";
        studentUserRepository.save(StudentUser.builder()
                .student(student)
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .passwordChanged(false)
                .status(StudentUser.Status.ACTIVE)
                .build());
        log.info("Created student login '{}' (default password {}@123)", username, username);
    }

    private String usernameBase(String email, String name) {
        if (email != null && email.contains("@")) return sanitize(email.substring(0, email.indexOf('@')));
        return sanitize(name);
    }

    private String sanitize(String s) {
        if (s == null) return "user";
        String out = s.toLowerCase().replaceAll("[^a-z0-9]", "");
        return out.isBlank() ? "user" : out;
    }

    private String uniqueStudentUsername(String base) {
        String username = base;
        int i = 0;
        while (studentUserRepository.existsByUsername(username)) {
            i++;
            username = base + i;
        }
        return username;
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
        existing.setPhotoBase64(updated.getPhotoBase64());
        // Newly-collected details
        existing.setFatherName(updated.getFatherName());
        existing.setMotherName(updated.getMotherName());
        existing.setFatherOccupation(updated.getFatherOccupation());
        existing.setMotherOccupation(updated.getMotherOccupation());
        existing.setEmergencyContact(updated.getEmergencyContact());
        existing.setAadharNumber(updated.getAadharNumber());
        existing.setCategory(updated.getCategory());
        existing.setNationality(updated.getNationality());
        existing.setReligion(updated.getReligion());
        existing.setAdmissionDate(updated.getAdmissionDate());
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