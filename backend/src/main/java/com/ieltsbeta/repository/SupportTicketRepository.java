package com.ieltsbeta.repository;

import com.ieltsbeta.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByStudent_StudentId(Long studentId);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
