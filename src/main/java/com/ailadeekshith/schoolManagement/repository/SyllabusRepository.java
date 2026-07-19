package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {

    List<Syllabus> findByGradeNameOrderBySubjectNameAsc(String gradeName);

    List<Syllabus> findByAcademicYearOrderByGradeNameAscSubjectNameAsc(String academicYear);

    Optional<Syllabus> findByGradeNameAndSubjectNameAndAcademicYear(
            String gradeName, String subjectName, String academicYear);

    boolean existsByGradeNameAndSubjectNameAndAcademicYear(
            String gradeName, String subjectName, String academicYear);
}
