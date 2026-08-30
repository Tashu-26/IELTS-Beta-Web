package com.ieltsbeta.dto;

public record AttemptStartResponse(
        Long attemptId,
        PracticeTestDetailResponse test
) {}
