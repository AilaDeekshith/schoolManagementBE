package com.ailadeekshith.schoolManagement.config;

import com.ailadeekshith.schoolManagement.dto.*;
import com.ailadeekshith.schoolManagement.model.*;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    // ── Student ───────────────────────────────────────────────

    public Student toStudent(StudentRequestDTO dto) {
        return Student.builder()
                .name(dto.getName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .bloodGroup(dto.getBloodGroup())
                .address(dto.getAddress())
                .nationality(dto.getNationality())
                .religion(dto.getReligion())
                .email(dto.getEmail())
                .className(dto.getClassName())
                .rollNumber(dto.getRollNumber())
                .admissionDate(dto.getAdmissionDate())
                .busRoute(dto.getBusRoute())
                .medicalNotes(dto.getMedicalNotes())
                .guardianName(dto.getGuardianName())
                .contactNumber(dto.getContactNumber())
                .build();
    }

    public StudentResponseDTO toStudentResponse(Student s) {
        return StudentResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .dob(s.getDob())
                .gender(s.getGender())
                .bloodGroup(s.getBloodGroup())
                .address(s.getAddress())
                .nationality(s.getNationality())
                .religion(s.getReligion())
                .email(s.getEmail())
                .className(s.getClassName())
                .rollNumber(s.getRollNumber())
                .admissionDate(s.getAdmissionDate())
                .busRoute(s.getBusRoute())
                .medicalNotes(s.getMedicalNotes())
                .guardianName(s.getGuardianName())
                .contactNumber(s.getContactNumber())
                .status(s.getStatus())
                .feeStatus(s.getFeeStatus())
                .photoBase64(s.getPhotoBase64())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    // ── Teacher ───────────────────────────────────────────────

    public Teacher toTeacher(TeacherRequestDTO dto) {
        return Teacher.builder()
                .name(dto.getName())
                .subject(dto.getSubject())
                .email(dto.getEmail())
                .contactNumber(dto.getContactNumber())
                .qualification(dto.getQualification())
                .experience(dto.getExperience())
                .assignedClasses(dto.getAssignedClasses())
                .build();
    }

    public TeacherResponseDTO toTeacherResponse(Teacher t) {
        return TeacherResponseDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .subject(t.getSubject())
                .email(t.getEmail())
                .contactNumber(t.getContactNumber())
                .qualification(t.getQualification())
                .experience(t.getExperience())
                .assignedClasses(t.getAssignedClasses())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }

    // ── Admission ─────────────────────────────────────────────

    public Admission toAdmission(AdmissionRequestDTO dto) {
        return Admission.builder()
                .applicantName(dto.getApplicantName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .applyClass(dto.getApplyClass())
                .guardianName(dto.getGuardianName())
                .contactNumber(dto.getContactNumber())
                .guardianEmail(dto.getGuardianEmail())
                .prevSchool(dto.getPrevSchool())
                .reason(dto.getReason())
                .build();
    }

    public AdmissionResponseDTO toAdmissionResponse(Admission a) {
        return AdmissionResponseDTO.builder()
                .id(a.getId())
                .applicantName(a.getApplicantName())
                .dob(a.getDob())
                .gender(a.getGender())
                .applyClass(a.getApplyClass())
                .guardianName(a.getGuardianName())
                .contactNumber(a.getContactNumber())
                .guardianEmail(a.getGuardianEmail())
                .prevSchool(a.getPrevSchool())
                .reason(a.getReason())
                .status(a.getStatus())
                .appliedDate(a.getAppliedDate())
                .studentId(a.getStudent() != null ? a.getStudent().getId() : null)
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    // ── ClassRoom ─────────────────────────────────────────────

    public ClassRoom toClassRoom(ClassRoomRequestDTO dto) {
        return ClassRoom.builder()
                .className(dto.getClassName())
                .roomNumber(dto.getRoomNumber())
                .maxCapacity(dto.getMaxCapacity() != null ? dto.getMaxCapacity() : 40)
                .classMonitor(dto.getClassMonitor())
                .build();
    }

    public ClassRoomResponseDTO toClassRoomResponse(ClassRoom c) {
        ClassRoomResponseDTO.ClassRoomResponseDTOBuilder builder = ClassRoomResponseDTO.builder()
                .id(c.getId())
                .className(c.getClassName())
                .roomNumber(c.getRoomNumber())
                .maxCapacity(c.getMaxCapacity())
                .currentStrength(c.getCurrentStrength())
                .classMonitor(c.getClassMonitor())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt());

        if (c.getClassTeacher() != null) {
            builder.classTeacherId(c.getClassTeacher().getId())
                    .classTeacherName(c.getClassTeacher().getName())
                    .classTeacherSubject(c.getClassTeacher().getSubject());
        }
        return builder.build();
    }

    // ── TimeTable ─────────────────────────────────────────────

    public TimeTable toTimeTable(TimeTableRequestDTO dto) {
        return TimeTable.builder()
                .className(dto.getClassName())
                .dayOfWeek(dto.getDayOfWeek())
                .periodNumber(dto.getPeriodNumber())
                .subject(dto.getSubject())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }

    public TimeTableResponseDTO toTimeTableResponse(TimeTable t) {
        TimeTableResponseDTO.TimeTableResponseDTOBuilder builder = TimeTableResponseDTO.builder()
                .id(t.getId())
                .className(t.getClassName())
                .dayOfWeek(t.getDayOfWeek())
                .periodNumber(t.getPeriodNumber())
                .subject(t.getSubject())
                .startTime(t.getStartTime())
                .endTime(t.getEndTime())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt());

        if (t.getTeacher() != null) {
            builder.teacherId(t.getTeacher().getId())
                    .teacherName(t.getTeacher().getName());
        }
        return builder.build();
    }

    // ── Fees ──────────────────────────────────────────────────

    public Fees toFees(FeesRequestDTO dto) {
        Student student = Student.builder().id(dto.getStudentId()).build();
        return Fees.builder()
                .student(student)
                .totalAmount(dto.getTotalAmount())
                .paidAmount(dto.getPaidAmount())
                .academicYear(dto.getAcademicYear())
                .feeType(dto.getFeeType())
                .dueDate(dto.getDueDate())
                .build();
    }

    public FeesResponseDTO toFeesResponse(Fees f) {
        FeesResponseDTO.FeesResponseDTOBuilder builder = FeesResponseDTO.builder()
                .id(f.getId())
                .totalAmount(f.getTotalAmount())
                .paidAmount(f.getPaidAmount())
                .dueAmount(f.getDueAmount())
                .academicYear(f.getAcademicYear())
                .feeType(f.getFeeType())
                .feeStatus(f.getFeeStatus())
                .dueDate(f.getDueDate())
                .paymentDate(f.getPaymentDate())
                .paymentMethod(f.getPaymentMethod())
                .transactionId(f.getTransactionId())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt());

        if (f.getStudent() != null) {
            builder.studentId(f.getStudent().getId())
                    .studentName(f.getStudent().getName())
                    .studentClass(f.getStudent().getClassName());
        }
        return builder.build();
    }

    // ── Exam ──────────────────────────────────────────────────

    public Exam toExam(ExamRequestDTO dto) {
        return Exam.builder()
                .name(dto.getName())
                .subject(dto.getSubject())
                .className(dto.getClassName())
                .examDate(dto.getExamDate())
                .maxMarks(dto.getMaxMarks())
                .duration(dto.getDuration())
                .instructions(dto.getInstructions())
                .build();
    }

    public ExamResponseDTO toExamResponse(Exam e) {
        ExamResponseDTO.ExamResponseDTOBuilder builder = ExamResponseDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .subject(e.getSubject())
                .className(e.getClassName())
                .examDate(e.getExamDate())
                .maxMarks(e.getMaxMarks())
                .duration(e.getDuration())
                .instructions(e.getInstructions())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt());

        if (e.getExaminer() != null) {
            builder.examinerId(e.getExaminer().getId())
                    .examinerName(e.getExaminer().getName());
        }
        return builder.build();
    }
}