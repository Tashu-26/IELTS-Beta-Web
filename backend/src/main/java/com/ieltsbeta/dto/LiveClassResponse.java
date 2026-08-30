package com.ieltsbeta.dto;

import java.time.OffsetDateTime;

public record LiveClassResponse(
        Long classId,
        Long courseId,
        String courseTitle,
        String meetingLink,
        OffsetDateTime classDate
) {}
