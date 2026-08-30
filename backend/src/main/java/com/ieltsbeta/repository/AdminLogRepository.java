package com.ieltsbeta.repository;

import com.ieltsbeta.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
    List<AdminLog> findAllByOrderByLoggedAtDesc();
}
