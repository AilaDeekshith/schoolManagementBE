package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.SeatAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatAssignmentRepository extends JpaRepository<SeatAssignment, Long> {

    List<SeatAssignment> findByClassRoomId(Long classRoomId);

    Optional<SeatAssignment> findByClassRoomIdAndRowNumAndColNumAndSeatIndex(
            Long classRoomId, int rowNum, int colNum, int seatIndex);

    void deleteByClassRoomId(Long classRoomId);
}
