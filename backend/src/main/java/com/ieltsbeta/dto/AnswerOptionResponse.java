package com.ieltsbeta.dto;

/** isCorrect is null when returned to a Student (hidden); populated for Teacher/Admin. */
public record AnswerOptionResponse(
        Long optionId,
        String optionText,
        Boolean isCorrect
) {}
