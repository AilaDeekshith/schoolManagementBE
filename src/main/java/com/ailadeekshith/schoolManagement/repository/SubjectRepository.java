package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);
    Optional<Subject> findByNameIgnoreCase(String name);
}
