package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    List<ExamSchedule> findByExamIdOrderByExamDateAscStartTimeAsc(Long examId);
    void deleteByExamId(Long examId);
}
