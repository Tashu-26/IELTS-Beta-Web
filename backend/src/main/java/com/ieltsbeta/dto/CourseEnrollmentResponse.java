package com.ieltsbeta.dto;

import java.time.OffsetDateTime;

public record CourseEnrollmentResponse(
        Long enrollmentId,
        Long studentId,
        String studentName,
        String status,
        OffsetDateTime enrolledAt
) {}
