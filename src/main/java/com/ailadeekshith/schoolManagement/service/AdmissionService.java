package com.ailadeekshith.schoolManagement.service;


import com.ailadeekshith.schoolManagement.model.Admission;

import java.util.List;

public interface AdmissionService {
    Admission createAdmission(Admission admission);
    Admission getAdmissionById(Long id);
    List<Admission> getAllAdmissions();
    Admission updateAdmission(Long id, Admission admission);
    void deleteAdmission(Long id);

    Admission approveAdmission(Long id);
    Admission rejectAdmission(Long id);
    Admission updateStatus(Long id, Admission.AdmissionStatus status);

    List<Admission> getAdmissionsByStatus(Admission.AdmissionStatus status);
    List<Admission> getAdmissionsByClass(String applyClass);
    List<Admission> searchByApplicantName(String name);
    long countPendingAdmissions();
}