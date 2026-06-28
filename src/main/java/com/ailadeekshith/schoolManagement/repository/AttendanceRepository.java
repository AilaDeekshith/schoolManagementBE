package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByClassNameAndDate(String className, LocalDate date);
    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate from, LocalDate to);
    Optional<Attendance> findByStudentIdAndClassNameAndDate(Long studentId, String className, LocalDate date);
    List<Attendance> findByClassNameAndDateBetween(String className, LocalDate from, LocalDate to);
}
