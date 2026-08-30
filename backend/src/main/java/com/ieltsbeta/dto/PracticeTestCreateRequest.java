package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PracticeTestCreateRequest(
        @NotNull Long courseId,
        @NotBlank String title,
        @NotBlank String category,
        Integer duration,
        Integer totalMarks
) {}
