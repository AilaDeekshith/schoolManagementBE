package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    List<AcademicYear> findAllByOrderByYearDesc();
    boolean existsByYear(String year);
}
