package com.ieltsbeta.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProgressResponse(
        BigDecimal targetBand,
        BigDecimal currentBand,
        int testsCompleted,
        BigDecimal avgListening,
        BigDecimal avgReading,
        BigDecimal avgWriting,
        BigDecimal avgSpeaking,
        List<AttemptSummaryResponse> recentAttempts
) {}
