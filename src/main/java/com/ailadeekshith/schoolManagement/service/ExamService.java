package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Exam;

import java.time.LocalDate;
import java.util.List;

public interface ExamService {
    Exam createExam(Exam exam);
    Exam getExamById(Long id);
    List<Exam> getAllExams();
    Exam updateExam(Long id, Exam exam);
    void deleteExam(Long id);

    Exam updateStatus(Long id, Exam.ExamStatus status);
    List<Exam> getExamsByClass(String className);
    List<Exam> getExamsByStatus(Exam.ExamStatus status);
    List<Exam> getExamsBySubject(String subject);
    List<Exam> getExamsBetweenDates(LocalDate from, LocalDate to);
    List<Exam> getUpcomingExams();
    List<Exam> getUpcomingExamsByClass(String className);
}