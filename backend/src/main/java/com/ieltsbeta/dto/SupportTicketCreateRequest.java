package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

public record SupportTicketCreateRequest(
        @NotBlank String subject,
        @NotBlank String message
) {}
