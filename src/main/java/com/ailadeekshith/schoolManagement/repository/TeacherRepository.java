package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findBySubject(String subject);

    List<Teacher> findByStatus(Teacher.TeacherStatus status);

    Optional<Teacher> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Teacher> findByNameContainingIgnoreCase(String name);

    List<Teacher> findByAssignedClassesContaining(String className);
}