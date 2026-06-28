package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.dto.SeatAssignmentDTO;
import com.ailadeekshith.schoolManagement.model.ClassRoom;

import java.util.List;

public interface SeatAssignmentService {
    ClassRoom updateLayout(Long classRoomId, Integer rows, Integer columns, Integer studentsPerBench);
    List<SeatAssignmentDTO> getSeatsForClass(Long classRoomId);
    SeatAssignmentDTO assignStudent(Long classRoomId, int rowNum, int colNum, int seatIndex, Long studentId);
    void unassignStudent(Long classRoomId, int rowNum, int colNum, int seatIndex);
}
