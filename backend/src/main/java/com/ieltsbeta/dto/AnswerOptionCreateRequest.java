package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerOptionCreateRequest(
        @NotBlank String optionText,
        boolean isCorrect
) {}
