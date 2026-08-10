package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.dto.ExamSeatDTO;
import com.ailadeekshith.schoolManagement.dto.ExamSeatingPlanDTO;
import com.ailadeekshith.schoolManagement.dto.ExamSeatingPlanRequest;
import com.ailadeekshith.schoolManagement.dto.ExamSessionDTO;

import java.util.List;

public interface ExamSeatingService {

    // ── Plans (rooms) ────────────────────────────────────────
    List<ExamSeatingPlanDTO> getPlansByExam(Long examId);
    ExamSeatingPlanDTO getPlan(Long planId);
    ExamSeatingPlanDTO createPlan(ExamSeatingPlanRequest request);
    ExamSeatingPlanDTO updatePlan(Long planId, ExamSeatingPlanRequest request);
    void deletePlan(Long planId);

    // ── Sessions (time periods within a room) ────────────────
    List<ExamSessionDTO> getSessions(Long planId);
    ExamSessionDTO addSession(Long planId, ExamSessionDTO dto);
    ExamSessionDTO updateSession(Long sessionId, ExamSessionDTO dto);
    void deleteSession(Long sessionId);

    // ── Seats ────────────────────────────────────────────────
    List<ExamSeatDTO> getSeats(Long planId);
    ExamSeatDTO assignStudent(Long planId, int rowNum, int colNum, int seatIndex, Long studentId);
    void unassignStudent(Long planId, int rowNum, int colNum, int seatIndex);
    List<ExamSeatDTO> autoAssign(Long planId);
    void clearSeats(Long planId);
}
