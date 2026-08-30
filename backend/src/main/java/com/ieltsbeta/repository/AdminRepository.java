package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser_UserId(Long userId);
}
