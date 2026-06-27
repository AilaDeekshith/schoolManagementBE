package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.TimeTable;

import java.util.List;

public interface TimeTableService {
    TimeTable createEntry(TimeTable timeTable);
    TimeTable getEntryById(Long id);
    List<TimeTable> getAllEntries();
    TimeTable updateEntry(Long id, TimeTable timeTable);
    void deleteEntry(Long id);

    List<TimeTable> getScheduleByClass(String className);
    List<TimeTable> getScheduleByClassAndDay(String className, TimeTable.DayOfWeek day);
    List<TimeTable> getScheduleByTeacher(Long teacherId);
    List<TimeTable> getTeacherScheduleForDay(Long teacherId, TimeTable.DayOfWeek day);
}