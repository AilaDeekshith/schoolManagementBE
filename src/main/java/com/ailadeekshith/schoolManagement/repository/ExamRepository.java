package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Exams now hold a list of classes; match on collection membership.
    @Query("SELECT e FROM Exam e JOIN e.classes c WHERE c = :className")
    List<Exam> findByClassName(String className);

    List<Exam> findByStatus(Exam.ExamStatus status);

    // Combined, all-optional filter run entirely in the database.
    // A null/absent argument means "don't filter on this field".
    @Query("SELECT e FROM Exam e WHERE " +
           "(:academicYear IS NULL OR e.academicYear = :academicYear) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:className IS NULL OR :className MEMBER OF e.classes) " +
           "ORDER BY e.id DESC")
    List<Exam> filterExams(String academicYear, Exam.ExamStatus status, String className);

    // Exams now hold a list of subjects; match on collection membership.
    @Query("SELECT e FROM Exam e JOIN e.subjects s WHERE s = :subject")
    List<Exam> findBySubject(String subject);

    List<Exam> findByExamDateBetween(LocalDate from, LocalDate to);


    @Query("SELECT e FROM Exam e WHERE e.examDate >= CURRENT_DATE AND e.status != 'COMPLETED' ORDER BY e.examDate ASC")
    List<Exam> findUpcomingExams();

    @Query("SELECT e FROM Exam e JOIN e.classes c WHERE c = :className AND e.status != 'COMPLETED' ORDER BY e.examDate ASC")
    List<Exam> findUpcomingExamsByClass(String className);
}