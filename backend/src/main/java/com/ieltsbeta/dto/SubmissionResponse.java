package com.ieltsbeta.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SubmissionResponse(
        Long submissionId,
        Long courseId,
        String courseTitle,
        String skill,
        String submissionType,
        String textContent,
        String audioUrl,
        String status,
        BigDecimal bandScore,
        String feedback,
        String gradedByName,
        Long studentId,
        String studentName,
        OffsetDateTime submittedAt,
        OffsetDateTime gradedAt
) {}
