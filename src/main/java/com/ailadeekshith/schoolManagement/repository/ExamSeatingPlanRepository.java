package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.ExamSeatingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSeatingPlanRepository extends JpaRepository<ExamSeatingPlan, Long> {
    List<ExamSeatingPlan> findByExamIdOrderByRoomNameAsc(Long examId);
    boolean existsByExamIdAndRoomNameIgnoreCase(Long examId, String roomName);
}
