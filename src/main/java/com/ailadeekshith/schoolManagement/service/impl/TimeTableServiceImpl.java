package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.model.TimeTable;
import com.ailadeekshith.schoolManagement.repository.TeacherRepository;
import com.ailadeekshith.schoolManagement.repository.TimeTableRepository;
import com.ailadeekshith.schoolManagement.service.TimeTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TimeTableServiceImpl implements TimeTableService {

    private final TimeTableRepository timeTableRepository;
    private final TeacherRepository teacherRepository;

    // ── Resolve teacherId → Teacher entity ───────────────────
    // This is the core fix: reads the @Transient teacherId from the
    // request body and fetches the actual Teacher row from the DB,
    // then sets it on the entity before saving.
    private void resolveTeacher(TimeTable timeTable) {
        Long tid = timeTable.getTeacherId();
        if (tid != null) {
            Teacher teacher = teacherRepository.findById(tid)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + tid));
            timeTable.setTeacher(teacher);
            log.info("Resolved teacherId {} to teacher: {}", tid, teacher.getName());
        } else {
            timeTable.setTeacher(null);
        }
    }

    @Override
    public TimeTable createEntry(TimeTable timeTable) {
        log.info("Creating timetable entry: {} {} P{}",
                timeTable.getClassName(), timeTable.getDayOfWeek(), timeTable.getPeriodNumber());

        if (timeTableRepository.existsByClassNameAndDayOfWeekAndPeriodNumber(
                timeTable.getClassName(), timeTable.getDayOfWeek(), timeTable.getPeriodNumber())) {
            throw new DuplicateResourceException(
                    "Timetable slot already exists for " + timeTable.getClassName()
                            + " on " + timeTable.getDayOfWeek()
                            + " period " + timeTable.getPeriodNumber());
        }

        // ── FIX: resolve teacherId before save ──
        resolveTeacher(timeTable);

        return timeTableRepository.save(timeTable);
    }

    @Override
    @Transactional(readOnly = true)
    public TimeTable getEntryById(Long id) {
        return timeTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeTable> getAllEntries() {
        return timeTableRepository.findAll();
    }

    @Override
    public TimeTable updateEntry(Long id, TimeTable updated) {
        TimeTable existing = getEntryById(id);
        existing.setSubject(updated.getSubject());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());

        // ── FIX: resolve teacherId on update too ──
        Long tid = updated.getTeacherId();
        if (tid != null) {
            Teacher teacher = teacherRepository.findById(tid)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + tid));
            existing.setTeacher(teacher);
            log.info("Updated teacher on timetable entry {} → {}", id, teacher.getName());
        } else {
            // If teacherId explicitly sent as null, clear the teacher
            existing.setTeacher(null);
        }

        log.info("Updated timetable entry id: {}", id);
        return timeTableRepository.save(existing);
    }

    @Override
    public void deleteEntry(Long id) {
        TimeTable entry = getEntryById(id);
        timeTableRepository.delete(entry);
        log.info("Deleted timetable entry id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeTable> getScheduleByClass(String className) {
        return timeTableRepository.findByClassNameOrderByDayOfWeekAscPeriodNumberAsc(className);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeTable> getScheduleByClassAndDay(String className, TimeTable.DayOfWeek day) {
        return timeTableRepository.findByClassNameAndDayOfWeek(className, day);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeTable> getScheduleByTeacher(Long teacherId) {
        return timeTableRepository.findByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeTable> getTeacherScheduleForDay(Long teacherId, TimeTable.DayOfWeek day) {
        return timeTableRepository.findTeacherScheduleForDay(teacherId, day);
    }
}