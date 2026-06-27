package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeesRepository extends JpaRepository<Fees, Long> {

    List<Fees> findByStudentId(Long studentId);

    List<Fees> findByFeeStatus(Fees.FeeStatus feeStatus);

    List<Fees> findByAcademicYear(String academicYear);

    Optional<Fees> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    @Query("SELECT SUM(f.paidAmount) FROM Fees f WHERE f.feeStatus = 'PAID'")
    BigDecimal getTotalCollected();

    @Query("SELECT SUM(f.dueAmount) FROM Fees f WHERE f.feeStatus != 'PAID'")
    BigDecimal getTotalOutstanding();

    @Query("SELECT f FROM Fees f WHERE f.student.className = :className")
    List<Fees> findByStudentClass(String className);
}