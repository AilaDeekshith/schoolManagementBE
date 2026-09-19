package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.ClassDiaryEntry;
import com.ailadeekshith.schoolManagement.model.Teacher;

import java.time.LocalDate;
import java.util.List;

public interface ClassDiaryService {

    List<ClassDiaryEntry> getByClassAndDate(String className, LocalDate date);
    List<ClassDiaryEntry> getByClassAndDateRange(String className, LocalDate from, LocalDate to);
    List<ClassDiaryEntry> getByTeacher(Long teacherId);
    ClassDiaryEntry getById(Long id);

    /** Creates or updates the entry for this (timetable slot, date) pair. */
    ClassDiaryEntry save(Long timetableId, LocalDate date, Teacher teacher, ClassDiaryEntry data);

    void delete(Long id);
}
