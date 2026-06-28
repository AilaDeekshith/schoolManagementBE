package com.ailadeekshith.schoolManagement.repository;

import com.ailadeekshith.schoolManagement.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<AppUser> findByRole(AppUser.UserRole role);
    List<AppUser> findByStatus(AppUser.UserStatus status);
    List<AppUser> findByNameContainingIgnoreCase(String name);
}
