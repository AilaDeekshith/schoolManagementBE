package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.ClassRoom;

import java.util.List;

public interface ClassRoomService {
    ClassRoom createClassRoom(ClassRoom classRoom);
    ClassRoom getClassRoomById(Long id);
    ClassRoom getClassRoomByName(String className);
    List<ClassRoom> getAllClassRooms();
    ClassRoom updateClassRoom(Long id, ClassRoom classRoom);
    void deleteClassRoom(Long id);

    ClassRoom assignClassTeacher(Long classRoomId, Long teacherId);
    ClassRoom incrementStrength(String className);
    ClassRoom decrementStrength(String className);
    List<ClassRoom> getClassesWithAvailableSeats();
}