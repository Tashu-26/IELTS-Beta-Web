package com.ieltsbeta.dto;

public record CourseResponse(
        Long courseId,
        String title,
        String description,
        String level,
        Integer duration
) {}
