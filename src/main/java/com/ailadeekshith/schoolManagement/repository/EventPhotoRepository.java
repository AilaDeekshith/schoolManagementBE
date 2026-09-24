package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {

    List<EventPhoto> findByEventIdOrderByIdAsc(Long eventId);

    void deleteByEventId(Long eventId);
}
