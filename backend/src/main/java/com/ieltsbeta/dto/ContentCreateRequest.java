package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

public record ContentCreateRequest(
        @NotBlank String title,
        @NotBlank String contentType,
        String youtubeLink,
        String fileUrl
) {}
