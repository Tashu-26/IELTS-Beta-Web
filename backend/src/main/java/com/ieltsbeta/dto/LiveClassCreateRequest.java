package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record LiveClassCreateRequest(
        @NotBlank String meetingLink,
        @NotNull OffsetDateTime classDate
) {}
