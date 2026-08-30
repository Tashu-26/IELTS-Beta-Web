package com.ieltsbeta.dto;

import java.time.OffsetDateTime;

public record SupportTicketResponse(
        Long ticketId,
        String subject,
        String message,
        String status,
        OffsetDateTime createdAt,
        Long studentId,
        String studentName,
        Long adminId
) {}
