package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClassName(String className);

    List<Student> findByStatus(Student.StudentStatus status);

    List<Student> findByFeeStatus(Student.FeeStatus feeStatus);

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Student> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Student s WHERE s.className = :className AND s.status = 'ACTIVE'")
    List<Student> findActiveStudentsByClass(String className);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = 'ACTIVE'")
    long countActiveStudents();

    @Query("SELECT s FROM Student s WHERE s.guardianName LIKE %:guardian%")
    List<Student> findByGuardianNameContaining(String guardian);
}