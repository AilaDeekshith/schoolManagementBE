package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.dto.ExamSeatDTO;
import com.ailadeekshith.schoolManagement.dto.ExamSeatingPlanDTO;
import com.ailadeekshith.schoolManagement.dto.ExamSeatingPlanRequest;
import com.ailadeekshith.schoolManagement.dto.SeatAssignRequest;
import com.ailadeekshith.schoolManagement.service.ExamSeatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-seating")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExamSeatingController {

    private final ExamSeatingService seatingService;

    // ── Plans (rooms) ─────────────────────────────────────────
    @GetMapping("/plans")
    public ResponseEntity<List<ExamSeatingPlanDTO>> getPlans(@RequestParam Long examId) {
        return ResponseEntity.ok(seatingService.getPlansByExam(examId));
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<ExamSeatingPlanDTO> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(seatingService.getPlan(id));
    }

    @PostMapping("/plans")
    public ResponseEntity<ExamSeatingPlanDTO> createPlan(@RequestBody ExamSeatingPlanRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatingService.createPlan(req));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<ExamSeatingPlanDTO> updatePlan(@PathVariable Long id,
                                                         @RequestBody ExamSeatingPlanRequest req) {
        return ResponseEntity.ok(seatingService.updatePlan(id, req));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        seatingService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    // ── Seats ─────────────────────────────────────────────────
    @GetMapping("/plans/{id}/seats")
    public ResponseEntity<List<ExamSeatDTO>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(seatingService.getSeats(id));
    }

    @PutMapping("/plans/{id}/seats/{row}/{col}/{seat}")
    public ResponseEntity<ExamSeatDTO> assignSeat(@PathVariable Long id,
                                                  @PathVariable int row,
                                                  @PathVariable int col,
                                                  @PathVariable int seat,
                                                  @RequestBody SeatAssignRequest request) {
        return ResponseEntity.ok(seatingService.assignStudent(id, row, col, seat, request.getStudentId()));
    }

    @DeleteMapping("/plans/{id}/seats/{row}/{col}/{seat}")
    public ResponseEntity<Void> unassignSeat(@PathVariable Long id,
                                             @PathVariable int row,
                                             @PathVariable int col,
                                             @PathVariable int seat) {
        seatingService.unassignStudent(id, row, col, seat);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/plans/{id}/auto-assign")
    public ResponseEntity<List<ExamSeatDTO>> autoAssign(@PathVariable Long id) {
        return ResponseEntity.ok(seatingService.autoAssign(id));
    }

    @DeleteMapping("/plans/{id}/seats")
    public ResponseEntity<Void> clearSeats(@PathVariable Long id) {
        seatingService.clearSeats(id);
        return ResponseEntity.noContent().build();
    }
}
