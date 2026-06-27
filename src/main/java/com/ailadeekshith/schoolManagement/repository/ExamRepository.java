package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByClassName(String className);

    List<Exam> findByStatus(Exam.ExamStatus status);

    List<Exam> findBySubject(String subject);

    List<Exam> findByExamDateBetween(LocalDate from, LocalDate to);

    List<Exam> findByExaminerId(Long teacherId);

    @Query("SELECT e FROM Exam e WHERE e.examDate >= CURRENT_DATE AND e.status != 'COMPLETED' ORDER BY e.examDate ASC")
    List<Exam> findUpcomingExams();

    @Query("SELECT e FROM Exam e WHERE e.className = :className AND e.status != 'COMPLETED' ORDER BY e.examDate ASC")
    List<Exam> findUpcomingExamsByClass(String className);
}