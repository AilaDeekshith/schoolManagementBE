package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Admission;
import com.ailadeekshith.schoolManagement.repository.AdmissionRepository;
import com.ailadeekshith.schoolManagement.service.AdmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdmissionServiceImpl implements AdmissionService {

    private final AdmissionRepository admissionRepository;

    @Override
    public Admission createAdmission(Admission admission) {
        log.info("Creating admission application for: {}", admission.getApplicantName());
        admission.setStatus(Admission.AdmissionStatus.PENDING);
        return admissionRepository.save(admission);
    }

    @Override
    @Transactional(readOnly = true)
    public Admission getAdmissionById(Long id) {
        return admissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admission> getAllAdmissions() {
        return admissionRepository.findAll();
    }

    @Override
    public Admission updateAdmission(Long id, Admission updated) {
        Admission existing = getAdmissionById(id);
        existing.setApplicantName(updated.getApplicantName());
        existing.setDob(updated.getDob());
        existing.setGender(updated.getGender());
        existing.setApplyClass(updated.getApplyClass());
        existing.setGuardianName(updated.getGuardianName());
        existing.setContactNumber(updated.getContactNumber());
        existing.setGuardianEmail(updated.getGuardianEmail());
        existing.setPrevSchool(updated.getPrevSchool());
        existing.setReason(updated.getReason());
        log.info("Updated admission id: {}", id);
        return admissionRepository.save(existing);
    }

    @Override
    public void deleteAdmission(Long id) {
        Admission admission = getAdmissionById(id);
        admissionRepository.delete(admission);
        log.info("Deleted admission id: {}", id);
    }

    @Override
    public Admission approveAdmission(Long id) {
        Admission admission = getAdmissionById(id);
        admission.setStatus(Admission.AdmissionStatus.APPROVED);
        log.info("Approved admission id: {}", id);
        return admissionRepository.save(admission);
    }

    @Override
    public Admission rejectAdmission(Long id) {
        Admission admission = getAdmissionById(id);
        admission.setStatus(Admission.AdmissionStatus.REJECTED);
        log.info("Rejected admission id: {}", id);
        return admissionRepository.save(admission);
    }

    @Override
    public Admission updateStatus(Long id, Admission.AdmissionStatus status) {
        Admission admission = getAdmissionById(id);
        admission.setStatus(status);
        log.info("Updated admission id: {} to status: {}", id, status);
        return admissionRepository.save(admission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admission> getAdmissionsByStatus(Admission.AdmissionStatus status) {
        return admissionRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admission> getAdmissionsByClass(String applyClass) {
        return admissionRepository.findByApplyClass(applyClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admission> searchByApplicantName(String name) {
        return admissionRepository.findByApplicantNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingAdmissions() {
        return admissionRepository.countPendingAdmissions();
    }
}