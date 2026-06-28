package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByAcademicYear(String academicYear);
    List<FeeStructure> findByGradeName(String gradeName);
}
