package com.ieltsbeta.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AttemptSummaryResponse(
        Long attemptId,
        Long testId,
        String testTitle,
        String category,
        OffsetDateTime submitTime,
        BigDecimal bandScore
) {}
