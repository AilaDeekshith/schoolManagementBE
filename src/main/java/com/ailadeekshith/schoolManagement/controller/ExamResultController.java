package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Exam;
import com.ailadeekshith.schoolManagement.model.ExamResult;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.repository.ExamRepository;
import com.ailadeekshith.schoolManagement.repository.ExamResultRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-results")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExamResultController {

    private final ExamResultRepository resultRepo;
    private final ExamRepository       examRepo;
    private final StudentRepository    studentRepo;

    /** Save / update marks for a batch of students (one subject at a time). */
    @PostMapping("/bulk")
    public ResponseEntity<List<ExamResult>> saveBulk(@RequestBody BulkRequest req) {
        Exam exam = examRepo.findById(req.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        List<ExamResult> saved = new ArrayList<>();
        for (ResultEntry entry : req.getEntries()) {
            Student student = studentRepo.findById(entry.getStudentId()).orElse(null);
            if (student == null) continue;

            ExamResult result = resultRepo
                    .findByExamIdAndStudentIdAndSubject(req.getExamId(), entry.getStudentId(), req.getSubject())
                    .orElseGet(() -> ExamResult.builder()
                            .exam(exam).student(student).subject(req.getSubject()).build());

            result.setMarksObtained(entry.getMarksObtained());
            result.setMaxMarks(exam.getMaxMarks());
            result.setGrade(entry.getMarksObtained() != null
                    ? grade(entry.getMarksObtained(), exam.getMaxMarks()) : null);
            result.setRemarks(entry.getRemarks());
            saved.add(resultRepo.save(result));
        }
        return ResponseEntity.ok(saved);
    }

    /** All results for a given exam. */
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamResult>> byExam(@PathVariable Long examId) {
        return ResponseEntity.ok(resultRepo.findByExamId(examId));
    }

    /** Results for a specific exam + subject. */
    @GetMapping("/exam/{examId}/subject/{subject}")
    public ResponseEntity<List<ExamResult>> byExamSubject(
            @PathVariable Long examId, @PathVariable String subject) {
        return ResponseEntity.ok(resultRepo.findByExamIdAndSubject(examId, subject));
    }

    /** All exam results for a student (across all exams). */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ExamResult>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(resultRepo.findByStudentId(studentId));
    }

    /** A student's results in one exam (all subjects). */
    @GetMapping("/student/{studentId}/exam/{examId}")
    public ResponseEntity<List<ExamResult>> studentExam(
            @PathVariable Long studentId, @PathVariable Long examId) {
        return ResponseEntity.ok(resultRepo.findByStudentIdAndExamId(studentId, examId));
    }

    /** Delete a single result entry. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Grade helper ─────────────────────────────────────────
    private static String grade(double marks, int max) {
        if (max == 0) return "—";
        double pct = (marks / max) * 100;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }

    // ── DTOs ─────────────────────────────────────────────────
    @Data
    public static class BulkRequest {
        private Long         examId;
        private String       subject;
        private List<ResultEntry> entries;
    }

    @Data
    public static class ResultEntry {
        private Long   studentId;
        private Double marksObtained;
        private String remarks;
    }
}
