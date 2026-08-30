package com.ieltsbeta.dto;

import java.time.OffsetDateTime;

public record AdminUserResponse(
        Long userId,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean isActive,
        OffsetDateTime createdAt
) {}
