package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Exam;
import com.ailadeekshith.schoolManagement.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExamController {

    private final ExamService examService;

    // POST /api/exams
    @PostMapping
    public ResponseEntity<Exam> createExam(@Valid @RequestBody Exam exam) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(exam));
    }

    // GET /api/exams
    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    // GET /api/exams/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    // PUT /api/exams/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody Exam exam) {
        return ResponseEntity.ok(examService.updateExam(id, exam));
    }

    // DELETE /api/exams/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/exams/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Exam> updateStatus(@PathVariable Long id,
                                             @RequestParam Exam.ExamStatus status) {
        return ResponseEntity.ok(examService.updateStatus(id, status));
    }

    // GET /api/exams/class/{className}
    @GetMapping("/class/{className}")
    public ResponseEntity<List<Exam>> getByClass(@PathVariable String className) {
        return ResponseEntity.ok(examService.getExamsByClass(className));
    }

    // GET /api/exams/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Exam>> getByStatus(@PathVariable Exam.ExamStatus status) {
        return ResponseEntity.ok(examService.getExamsByStatus(status));
    }

    // GET /api/exams/subject/{subject}
    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Exam>> getBySubject(@PathVariable String subject) {
        return ResponseEntity.ok(examService.getExamsBySubject(subject));
    }

    // GET /api/exams/upcoming
    @GetMapping("/upcoming")
    public ResponseEntity<List<Exam>> getUpcoming() {
        return ResponseEntity.ok(examService.getUpcomingExams());
    }

    // GET /api/exams/upcoming/class/{className}
    @GetMapping("/upcoming/class/{className}")
    public ResponseEntity<List<Exam>> getUpcomingByClass(@PathVariable String className) {
        return ResponseEntity.ok(examService.getUpcomingExamsByClass(className));
    }

    // GET /api/exams/range?from=2024-04-01&to=2024-06-30
    @GetMapping("/range")
    public ResponseEntity<List<Exam>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(examService.getExamsBetweenDates(from, to));
    }
}
