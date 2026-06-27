package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Fees;
import com.ailadeekshith.schoolManagement.model.Student;

import java.util.List;

public interface StudentService {
    Student createStudent(Student student);
    Student getStudentById(Long id);
    List<Student> getAllStudents();
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);

    List<Student> getStudentsByClass(String className);
    List<Student> getStudentsByStatus(Student.StudentStatus status);
    List<Student> getStudentsByFeeStatus(Student.FeeStatus feeStatus);
    List<Student> searchStudentsByName(String name);
    long countActiveStudents();
}