package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.DuplicateResourceException;
import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.ClassRoom;
import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.repository.ClassRoomRepository;
import com.ailadeekshith.schoolManagement.repository.TeacherRepository;
import com.ailadeekshith.schoolManagement.service.ClassRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassRoomServiceImpl implements ClassRoomService {

    private final ClassRoomRepository classRoomRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public ClassRoom createClassRoom(ClassRoom classRoom) {
        log.info("Creating class: {}", classRoom.getClassName());
        if (classRoomRepository.existsByClassName(classRoom.getClassName())) {
            throw new DuplicateResourceException("Class already exists: " + classRoom.getClassName());
        }
        if (classRoomRepository.existsByRoomNumber(classRoom.getRoomNumber())) {
            throw new DuplicateResourceException("Room number already in use: " + classRoom.getRoomNumber());
        }
        return classRoomRepository.save(classRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassRoom getClassRoomById(Long id) {
        return classRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClassRoom getClassRoomByName(String className) {
        return classRoomRepository.findByClassName(className)
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found: " + className));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassRoom> getAllClassRooms() {
        return classRoomRepository.findAll();
    }

    @Override
    public ClassRoom updateClassRoom(Long id, ClassRoom updated) {
        ClassRoom existing = getClassRoomById(id);
        existing.setRoomNumber(updated.getRoomNumber());
        existing.setMaxCapacity(updated.getMaxCapacity());
        existing.setClassMonitor(updated.getClassMonitor());
        log.info("Updated classroom id: {}", id);
        return classRoomRepository.save(existing);
    }

    @Override
    public void deleteClassRoom(Long id) {
        ClassRoom classRoom = getClassRoomById(id);
        classRoomRepository.delete(classRoom);
        log.info("Deleted classroom id: {}", id);
    }

    @Override
    public ClassRoom assignClassTeacher(Long classRoomId, Long teacherId) {
        ClassRoom classRoom = getClassRoomById(classRoomId);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        classRoom.setClassTeacher(teacher);
        log.info("Assigned teacher {} to class {}", teacher.getName(), classRoom.getClassName());
        return classRoomRepository.save(classRoom);
    }

    @Override
    public ClassRoom incrementStrength(String className) {
        ClassRoom classRoom = getClassRoomByName(className);
        classRoom.setCurrentStrength(classRoom.getCurrentStrength() + 1);
        return classRoomRepository.save(classRoom);
    }

    @Override
    public ClassRoom decrementStrength(String className) {
        ClassRoom classRoom = getClassRoomByName(className);
        if (classRoom.getCurrentStrength() > 0) {
            classRoom.setCurrentStrength(classRoom.getCurrentStrength() - 1);
        }
        return classRoomRepository.save(classRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassRoom> getClassesWithAvailableSeats() {
        return classRoomRepository.findClassesWithAvailableSeats();
    }
}