package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.SyllabusTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SyllabusTopicRepository extends JpaRepository<SyllabusTopic, Long> {

    List<SyllabusTopic> findBySyllabusIdOrderByOrderIndexAsc(Long syllabusId);

    @Query("SELECT COALESCE(MAX(t.orderIndex), -1) FROM SyllabusTopic t WHERE t.syllabus.id = :syllabusId")
    int findMaxOrderIndex(@Param("syllabusId") Long syllabusId);
}
