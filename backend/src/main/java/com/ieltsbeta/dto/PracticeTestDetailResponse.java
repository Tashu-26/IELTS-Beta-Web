package com.ieltsbeta.dto;

import java.util.List;

public record PracticeTestDetailResponse(
        Long testId,
        Long courseId,
        String title,
        String category,
        Integer duration,
        Integer totalMarks,
        List<QuestionResponse> questions
) {}
