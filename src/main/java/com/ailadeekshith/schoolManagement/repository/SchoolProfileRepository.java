package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.SchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolProfileRepository extends JpaRepository<SchoolProfile, Long> {}
