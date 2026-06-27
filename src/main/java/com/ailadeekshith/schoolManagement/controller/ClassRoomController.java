package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.ClassRoom;
import com.ailadeekshith.schoolManagement.service.ClassRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClassRoomController {

    private final ClassRoomService classRoomService;

    // POST /api/classes
    @PostMapping
    public ResponseEntity<ClassRoom> createClassRoom(@Valid @RequestBody ClassRoom classRoom) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classRoomService.createClassRoom(classRoom));
    }

    // GET /api/classes
    @GetMapping
    public ResponseEntity<List<ClassRoom>> getAllClassRooms() {
        return ResponseEntity.ok(classRoomService.getAllClassRooms());
    }

    // GET /api/classes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClassRoom> getClassRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(classRoomService.getClassRoomById(id));
    }

    // GET /api/classes/name/{className}
    @GetMapping("/name/{className}")
    public ResponseEntity<ClassRoom> getClassRoomByName(@PathVariable String className) {
        return ResponseEntity.ok(classRoomService.getClassRoomByName(className));
    }

    // PUT /api/classes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ClassRoom> updateClassRoom(@PathVariable Long id,
                                                     @Valid @RequestBody ClassRoom classRoom) {
        return ResponseEntity.ok(classRoomService.updateClassRoom(id, classRoom));
    }

    // DELETE /api/classes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassRoom(@PathVariable Long id) {
        classRoomService.deleteClassRoom(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/classes/{classRoomId}/assign-teacher/{teacherId}
    @PatchMapping("/{classRoomId}/assign-teacher/{teacherId}")
    public ResponseEntity<ClassRoom> assignTeacher(@PathVariable Long classRoomId,
                                                   @PathVariable Long teacherId) {
        return ResponseEntity.ok(classRoomService.assignClassTeacher(classRoomId, teacherId));
    }

    // GET /api/classes/available
    @GetMapping("/available")
    public ResponseEntity<List<ClassRoom>> getAvailableClasses() {
        return ResponseEntity.ok(classRoomService.getClassesWithAvailableSeats());
    }
}