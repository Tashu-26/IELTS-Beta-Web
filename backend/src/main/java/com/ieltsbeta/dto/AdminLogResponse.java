package com.ieltsbeta.dto;

import java.time.OffsetDateTime;

public record AdminLogResponse(
        Long logId,
        String action,
        String details,
        OffsetDateTime loggedAt,
        String adminName
) {}
