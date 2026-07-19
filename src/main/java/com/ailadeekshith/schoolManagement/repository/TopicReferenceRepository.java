package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.TopicReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicReferenceRepository extends JpaRepository<TopicReference, Long> {

    List<TopicReference> findByTopicIdOrderByIdAsc(Long topicId);
}
