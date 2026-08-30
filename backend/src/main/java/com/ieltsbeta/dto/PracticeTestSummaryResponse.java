package com.ieltsbeta.dto;

public record PracticeTestSummaryResponse(
        Long testId,
        Long courseId,
        String title,
        String category,
        Integer duration,
        Integer totalMarks
) {}
