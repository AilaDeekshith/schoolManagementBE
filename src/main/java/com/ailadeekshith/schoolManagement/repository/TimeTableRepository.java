package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    List<TimeTable> findByClassNameOrderByDayOfWeekAscPeriodNumberAsc(String className);

    List<TimeTable> findByDayOfWeek(TimeTable.DayOfWeek dayOfWeek);

    List<TimeTable> findByTeacherId(Long teacherId);

    List<TimeTable> findByClassNameAndDayOfWeek(String className, TimeTable.DayOfWeek dayOfWeek);

    Optional<TimeTable> findByClassNameAndDayOfWeekAndPeriodNumber(
            String className, TimeTable.DayOfWeek dayOfWeek, Integer periodNumber);

    boolean existsByClassNameAndDayOfWeekAndPeriodNumber(
            String className, TimeTable.DayOfWeek dayOfWeek, Integer periodNumber);

    @Query("SELECT t FROM TimeTable t WHERE t.teacher.id = :teacherId AND t.dayOfWeek = :day ORDER BY t.periodNumber")
    List<TimeTable> findTeacherScheduleForDay(Long teacherId, TimeTable.DayOfWeek day);
}