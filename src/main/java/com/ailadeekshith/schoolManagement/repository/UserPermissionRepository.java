package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
