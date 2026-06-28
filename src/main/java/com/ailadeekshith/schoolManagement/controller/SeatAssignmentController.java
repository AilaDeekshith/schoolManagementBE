package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.dto.LayoutUpdateDTO;
import com.ailadeekshith.schoolManagement.dto.SeatAssignRequest;
import com.ailadeekshith.schoolManagement.dto.SeatAssignmentDTO;
import com.ailadeekshith.schoolManagement.model.ClassRoom;
import com.ailadeekshith.schoolManagement.service.SeatAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SeatAssignmentController {

    private final SeatAssignmentService seatAssignmentService;

    // PUT /api/classes/{id}/layout
    @PutMapping("/{id}/layout")
    public ResponseEntity<ClassRoom> updateLayout(@PathVariable Long id,
                                                  @RequestBody LayoutUpdateDTO dto) {
        return ResponseEntity.ok(seatAssignmentService.updateLayout(
                id, dto.getRows(), dto.getColumns(), dto.getStudentsPerBench()));
    }

    // GET /api/classes/{id}/seats
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatAssignmentDTO>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(seatAssignmentService.getSeatsForClass(id));
    }

    // PUT /api/classes/{id}/seats/{row}/{col}/{seat}
    @PutMapping("/{id}/seats/{row}/{col}/{seat}")
    public ResponseEntity<SeatAssignmentDTO> assignSeat(@PathVariable Long id,
                                                        @PathVariable int row,
                                                        @PathVariable int col,
                                                        @PathVariable int seat,
                                                        @RequestBody SeatAssignRequest request) {
        return ResponseEntity.ok(seatAssignmentService.assignStudent(id, row, col, seat, request.getStudentId()));
    }

    // DELETE /api/classes/{id}/seats/{row}/{col}/{seat}
    @DeleteMapping("/{id}/seats/{row}/{col}/{seat}")
    public ResponseEntity<Void> unassignSeat(@PathVariable Long id,
                                             @PathVariable int row,
                                             @PathVariable int col,
                                             @PathVariable int seat) {
        seatAssignmentService.unassignStudent(id, row, col, seat);
        return ResponseEntity.noContent().build();
    }
}
