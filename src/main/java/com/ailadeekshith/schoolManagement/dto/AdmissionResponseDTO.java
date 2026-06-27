package com.ailadeekshith.schoolManagement.dto;
import com.ailadeekshith.schoolManagement.model.Admission;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionResponseDTO {

    private Long id;
    private String applicantName;
    private LocalDate dob;
    private String gender;
    private String applyClass;
    private String guardianName;
    private String contactNumber;
    private String guardianEmail;
    private String prevSchool;
    private String reason;
    private Admission.AdmissionStatus status;
    private LocalDate appliedDate;
    private Long studentId;           // set after approval
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}