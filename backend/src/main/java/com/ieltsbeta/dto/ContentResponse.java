package com.ieltsbeta.dto;

public record ContentResponse(
        Long contentId,
        Long courseId,
        String title,
        String contentType,
        String youtubeLink,
        String fileUrl
) {}
