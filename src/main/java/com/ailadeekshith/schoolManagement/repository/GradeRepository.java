package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByName(String name);
    List<Grade> findAllByOrderByDisplayOrderAsc();
}
