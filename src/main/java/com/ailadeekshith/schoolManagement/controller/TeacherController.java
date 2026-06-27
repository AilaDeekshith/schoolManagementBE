package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Teacher;
import com.ailadeekshith.schoolManagement.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeacherController {

    private final TeacherService teacherService;

    // POST /api/teachers
    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody Teacher teacher) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacher));
    }

    // GET /api/teachers
    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    // GET /api/teachers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    // PUT /api/teachers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id,
                                                 @Valid @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }

    // DELETE /api/teachers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/teachers/subject/{subject}
    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Teacher>> getBySubject(@PathVariable String subject) {
        return ResponseEntity.ok(teacherService.getTeachersBySubject(subject));
    }

    // GET /api/teachers/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Teacher>> getByStatus(@PathVariable Teacher.TeacherStatus status) {
        return ResponseEntity.ok(teacherService.getTeachersByStatus(status));
    }

    // GET /api/teachers/class/{className}
    @GetMapping("/class/{className}")
    public ResponseEntity<List<Teacher>> getByClass(@PathVariable String className) {
        return ResponseEntity.ok(teacherService.getTeachersByClass(className));
    }

    // GET /api/teachers/search?name=xyz
    @GetMapping("/search")
    public ResponseEntity<List<Teacher>> search(@RequestParam String name) {
        return ResponseEntity.ok(teacherService.searchTeachersByName(name));
    }
}
