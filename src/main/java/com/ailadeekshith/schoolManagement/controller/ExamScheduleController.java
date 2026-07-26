package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.dto.ExamScheduleDTO;
import com.ailadeekshith.schoolManagement.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExamScheduleController {

    private final ExamScheduleService scheduleService;

    // GET /api/exams/{examId}/schedule
    @GetMapping("/exams/{examId}/schedule")
    public ResponseEntity<List<ExamScheduleDTO>> getByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(scheduleService.getByExam(examId));
    }

    // POST /api/exams/{examId}/schedule
    @PostMapping("/exams/{examId}/schedule")
    public ResponseEntity<ExamScheduleDTO> create(@PathVariable Long examId,
                                                  @RequestBody ExamScheduleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(examId, dto));
    }

    // PUT /api/exam-schedule/{id}
    @PutMapping("/exam-schedule/{id}")
    public ResponseEntity<ExamScheduleDTO> update(@PathVariable Long id,
                                                  @RequestBody ExamScheduleDTO dto) {
        return ResponseEntity.ok(scheduleService.update(id, dto));
    }

    // DELETE /api/exam-schedule/{id}
    @DeleteMapping("/exam-schedule/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
