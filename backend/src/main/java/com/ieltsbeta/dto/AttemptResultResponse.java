package com.ieltsbeta.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AttemptResultResponse(
        Long attemptId,
        Long testId,
        String testTitle,
        OffsetDateTime startTime,
        OffsetDateTime submitTime,
        BigDecimal score,
        BigDecimal overallBand,
        BigDecimal listening,
        BigDecimal reading,
        BigDecimal writing,
        BigDecimal speaking,
        String feedback
) {}
