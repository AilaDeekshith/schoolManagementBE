package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    List<Admission> findByStatus(Admission.AdmissionStatus status);

    List<Admission> findByApplyClass(String applyClass);

    List<Admission> findByApplicantNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(a) FROM Admission a WHERE a.status = 'PENDING' OR a.status = 'UNDER_REVIEW'")
    long countPendingAdmissions();

    @Query("SELECT a FROM Admission a WHERE a.status = :status ORDER BY a.appliedDate DESC")
    List<Admission> findByStatusOrderByDate(Admission.AdmissionStatus status);
}