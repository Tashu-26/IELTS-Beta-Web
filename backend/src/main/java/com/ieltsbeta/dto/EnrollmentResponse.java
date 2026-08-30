package com.ieltsbeta.dto;

import java.time.OffsetDateTime;

public record EnrollmentResponse(
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        String status,
        OffsetDateTime enrolledAt
) {}
