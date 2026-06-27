package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {

    Optional<ClassRoom> findByClassName(String className);

    boolean existsByClassName(String className);

    boolean existsByRoomNumber(String roomNumber);

    List<ClassRoom> findByClassTeacherId(Long teacherId);

    @Query("SELECT c FROM ClassRoom c WHERE c.currentStrength < c.maxCapacity")
    List<ClassRoom> findClassesWithAvailableSeats();
}
