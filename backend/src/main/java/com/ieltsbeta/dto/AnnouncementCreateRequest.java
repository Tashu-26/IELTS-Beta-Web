package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

public record AnnouncementCreateRequest(
        @NotBlank String title,
        @NotBlank String message
) {}
