package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseCreateRequest(
        @NotBlank String title,
        String description,
        String level,
        Integer duration
) {}
