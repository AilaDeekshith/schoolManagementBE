package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.dto.SeatAssignmentDTO;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.ClassRoom;
import com.ailadeekshith.schoolManagement.model.SeatAssignment;
import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.repository.ClassRoomRepository;
import com.ailadeekshith.schoolManagement.repository.SeatAssignmentRepository;
import com.ailadeekshith.schoolManagement.repository.StudentRepository;
import com.ailadeekshith.schoolManagement.service.SeatAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatAssignmentServiceImpl implements SeatAssignmentService {

    private final ClassRoomRepository classRoomRepository;
    private final SeatAssignmentRepository seatAssignmentRepository;
    private final StudentRepository studentRepository;

    @Override
    public ClassRoom updateLayout(Long classRoomId, Integer rows, Integer columns, Integer studentsPerBench) {
        ClassRoom classRoom = classRoomRepository.findById(classRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found: " + classRoomId));
        classRoom.setRows(rows);
        classRoom.setColumns(columns);
        classRoom.setStudentsPerBench(studentsPerBench);
        seatAssignmentRepository.deleteByClassRoomId(classRoomId);
        return classRoomRepository.save(classRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatAssignmentDTO> getSeatsForClass(Long classRoomId) {
        return seatAssignmentRepository.findByClassRoomId(classRoomId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SeatAssignmentDTO assignStudent(Long classRoomId, int rowNum, int colNum, int seatIndex, Long studentId) {
        ClassRoom classRoom = classRoomRepository.findById(classRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found: " + classRoomId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        Optional<SeatAssignment> existing = seatAssignmentRepository
                .findByClassRoomIdAndRowNumAndColNumAndSeatIndex(classRoomId, rowNum, colNum, seatIndex);

        SeatAssignment seat = existing.orElse(SeatAssignment.builder()
                .classRoom(classRoom)
                .rowNum(rowNum)
                .colNum(colNum)
                .seatIndex(seatIndex)
                .build());
        seat.setStudent(student);
        return toDTO(seatAssignmentRepository.save(seat));
    }

    @Override
    public void unassignStudent(Long classRoomId, int rowNum, int colNum, int seatIndex) {
        seatAssignmentRepository
                .findByClassRoomIdAndRowNumAndColNumAndSeatIndex(classRoomId, rowNum, colNum, seatIndex)
                .ifPresent(seatAssignmentRepository::delete);
    }

    private SeatAssignmentDTO toDTO(SeatAssignment seat) {
        return SeatAssignmentDTO.builder()
                .id(seat.getId())
                .rowNum(seat.getRowNum())
                .colNum(seat.getColNum())
                .seatIndex(seat.getSeatIndex())
                .studentId(seat.getStudent() != null ? seat.getStudent().getId() : null)
                .studentName(seat.getStudent() != null ? seat.getStudent().getName() : null)
                .build();
    }
}
