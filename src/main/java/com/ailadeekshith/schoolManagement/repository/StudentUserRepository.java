package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentUserRepository extends JpaRepository<StudentUser, Long> {
    Optional<StudentUser> findByUsername(String username);
    boolean existsByUsername(String username);
    List<StudentUser> findByStudentId(Long studentId);
}
