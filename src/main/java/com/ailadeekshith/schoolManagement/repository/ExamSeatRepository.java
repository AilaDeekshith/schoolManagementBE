package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.ExamSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSeatRepository extends JpaRepository<ExamSeat, Long> {

    List<ExamSeat> findByPlanId(Long planId);

    Optional<ExamSeat> findByPlanIdAndRowNumAndColNumAndSeatIndex(
            Long planId, int rowNum, int colNum, int seatIndex);

    Optional<ExamSeat> findByPlanIdAndStudentId(Long planId, Long studentId);

    void deleteByPlanId(Long planId);
}
