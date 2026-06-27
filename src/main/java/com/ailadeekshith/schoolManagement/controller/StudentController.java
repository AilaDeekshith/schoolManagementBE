package com.ailadeekshith.schoolManagement.controller;

import com.ailadeekshith.schoolManagement.model.Student;
import com.ailadeekshith.schoolManagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    // POST /api/students
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(student));
    }

    // GET /api/students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // GET /api/students/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // PUT /api/students/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
                                                 @Valid @RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(id, student));
    }

    // DELETE /api/students/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/students/class/{className}
    @GetMapping("/class/{className}")
    public ResponseEntity<List<Student>> getByClass(@PathVariable String className) {
        return ResponseEntity.ok(studentService.getStudentsByClass(className));
    }

    // GET /api/students/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Student>> getByStatus(@PathVariable Student.StudentStatus status) {
        return ResponseEntity.ok(studentService.getStudentsByStatus(status));
    }

    // GET /api/students/fee-status/{feeStatus}
    @GetMapping("/fee-status/{feeStatus}")
    public ResponseEntity<List<Student>> getByFeeStatus(@PathVariable Student.FeeStatus feeStatus) {
        return ResponseEntity.ok(studentService.getStudentsByFeeStatus(feeStatus));
    }

    // GET /api/students/search?name=xyz
    @GetMapping("/search")
    public ResponseEntity<List<Student>> search(@RequestParam String name) {
        return ResponseEntity.ok(studentService.searchStudentsByName(name));
    }

    // GET /api/students/count/active
    @GetMapping("/count/active")
    public ResponseEntity<Long> countActive() {
        return ResponseEntity.ok(studentService.countActiveStudents());
    }
}