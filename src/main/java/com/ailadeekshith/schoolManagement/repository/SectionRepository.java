package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByGradeId(Long gradeId);
    boolean existsByGradeIdAndLetter(Long gradeId, String letter);
    Optional<Section> findByGradeIdAndLetterIgnoreCase(Long gradeId, String letter);
}
