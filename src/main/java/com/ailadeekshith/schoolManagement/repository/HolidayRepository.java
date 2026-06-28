package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findByAcademicYearOrderByDateAsc(String academicYear);
    List<Holiday> findAllByOrderByDateAsc();
}
