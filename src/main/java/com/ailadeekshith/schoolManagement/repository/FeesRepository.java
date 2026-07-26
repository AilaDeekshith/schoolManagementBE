package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Flexible search — any combination of class, academic year, status and name.
    // NOTE: :name is passed as "" (never null) for the "no filter" case. Binding a
    // null inside lower()/concat() makes Postgres type it as bytea and fail with
    // "function lower(bytea) does not exist", so we use an empty-string sentinel.
    @Query("SELECT f FROM Fees f WHERE " +
           "(:className IS NULL OR f.student.className = :className) AND " +
           "(:academicYear IS NULL OR f.academicYear = :academicYear) AND " +
           "(:status IS NULL OR f.feeStatus = :status) AND " +
           "(:name = '' OR LOWER(f.student.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "ORDER BY f.student.name ASC")
    List<Fees> search(@Param("className") String className,
                      @Param("academicYear") String academicYear,
                      @Param("status") Fees.FeeStatus status,
                      @Param("name") String name);

    @Query("SELECT DISTINCT f.academicYear FROM Fees f WHERE f.academicYear IS NOT NULL ORDER BY f.academicYear DESC")
    List<String> findDistinctAcademicYears();

}