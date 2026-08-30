package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionCreateRequest(
        @NotBlank String questionText,
        @NotBlank String skill,
        Integer marks
) {}
