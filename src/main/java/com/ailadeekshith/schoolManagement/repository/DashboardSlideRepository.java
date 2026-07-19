package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.DashboardSlide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardSlideRepository extends JpaRepository<DashboardSlide, Long> {
    List<DashboardSlide> findAllByOrderBySortOrderAscIdAsc();
}
