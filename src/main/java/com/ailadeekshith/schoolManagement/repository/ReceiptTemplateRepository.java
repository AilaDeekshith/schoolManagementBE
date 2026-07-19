package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.ReceiptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptTemplateRepository extends JpaRepository<ReceiptTemplate, Long> {
    List<ReceiptTemplate> findAllByOrderByIdAsc();
    Optional<ReceiptTemplate> findFirstByIsDefaultTrue();
}
