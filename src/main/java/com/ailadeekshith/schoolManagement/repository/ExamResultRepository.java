package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByExamId(Long examId);
    List<ExamResult> findByExamIdAndSubject(Long examId, String subject);
    List<ExamResult> findByStudentId(Long studentId);
    List<ExamResult> findByStudentIdAndExamId(Long studentId, Long examId);
    Optional<ExamResult> findByExamIdAndStudentIdAndSubject(Long examId, Long studentId, String subject);
    void deleteByExamIdAndStudentIdAndSubject(Long examId, Long studentId, String subject);
}
