package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

/** status must be one of: Open, In Progress, Resolved. */
public record SupportTicketUpdateRequest(
        @NotBlank String status
) {}
