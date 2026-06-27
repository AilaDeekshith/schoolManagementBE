package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Teacher;

import java.util.List;

public interface TeacherService {
    Teacher createTeacher(Teacher teacher);
    Teacher getTeacherById(Long id);
    List<Teacher> getAllTeachers();
    Teacher updateTeacher(Long id, Teacher teacher);
    void deleteTeacher(Long id);

    List<Teacher> getTeachersBySubject(String subject);
    List<Teacher> getTeachersByStatus(Teacher.TeacherStatus status);
    List<Teacher> searchTeachersByName(String name);
    List<Teacher> getTeachersByClass(String className);
}