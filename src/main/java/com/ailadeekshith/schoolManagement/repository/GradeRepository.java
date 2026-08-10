package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByName(String name);
    Optional<Grade> findByNameIgnoreCase(String name);
    List<Grade> findAllByOrderByDisplayOrderAsc();
}
