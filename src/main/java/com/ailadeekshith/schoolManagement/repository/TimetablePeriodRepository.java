package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.TimetablePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetablePeriodRepository extends JpaRepository<TimetablePeriod, Long> {

    List<TimetablePeriod> findByClassNameOrderByPeriodNumberAsc(String className);

    Optional<TimetablePeriod> findByClassNameAndPeriodNumber(String className, Integer periodNumber);

    boolean existsByClassNameAndPeriodNumber(String className, Integer periodNumber);

    @Modifying
    @Transactional
    @Query("DELETE FROM TimetablePeriod t WHERE t.className = :className")
    void deleteAllByClassName(String className);

    List<TimetablePeriod> findByClassNameAndIsBreakFalseOrderByPeriodNumberAsc(String className);
}