package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Exam;
import com.ailadeekshith.schoolManagement.repository.ExamRepository;
import com.ailadeekshith.schoolManagement.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final com.ailadeekshith.schoolManagement.service.ReferenceResolver referenceResolver;

    @Override
    public Exam createExam(Exam exam) {
        log.info("Scheduling exam: {}", exam.getName());
        exam.setSection(referenceResolver.resolveSection(exam.getClassName()));
        exam.setSubjectRef(referenceResolver.resolveSubject(exam.getSubject()));
        return examRepository.save(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public Exam getExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Override
    public Exam updateExam(Long id, Exam updated) {
        Exam existing = getExamById(id);
        existing.setName(updated.getName());
        existing.setSubject(updated.getSubject());
        existing.setClassName(updated.getClassName());
        existing.setSection(referenceResolver.resolveSection(updated.getClassName()));
        existing.setSubjectRef(referenceResolver.resolveSubject(updated.getSubject()));
        existing.setExamDate(updated.getExamDate());
        existing.setMaxMarks(updated.getMaxMarks());
        existing.setDuration(updated.getDuration());
        existing.setInstructions(updated.getInstructions());
        existing.setStatus(updated.getStatus());
        log.info("Updated exam id: {}", id);
        return examRepository.save(existing);
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = getExamById(id);
        examRepository.delete(exam);
        log.info("Deleted exam id: {}", id);
    }

    @Override
    public Exam updateStatus(Long id, Exam.ExamStatus status) {
        Exam exam = getExamById(id);
        exam.setStatus(status);
        log.info("Updated exam id: {} to status: {}", id, status);
        return examRepository.save(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getExamsByClass(String className) {
        return examRepository.findByClassName(className);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getExamsByStatus(Exam.ExamStatus status) {
        return examRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getExamsBySubject(String subject) {
        return examRepository.findBySubject(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getExamsBetweenDates(LocalDate from, LocalDate to) {
        return examRepository.findByExamDateBetween(from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getUpcomingExams() {
        return examRepository.findUpcomingExams();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getUpcomingExamsByClass(String className) {
        return examRepository.findUpcomingExamsByClass(className);
    }
}
