package com.ieltsbeta.dto;

/** selectedOptionId may be null if the student skipped this question. */
public record AnswerSubmissionDto(
        Long questionId,
        Long selectedOptionId
) {}
