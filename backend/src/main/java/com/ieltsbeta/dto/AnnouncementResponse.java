package com.ieltsbeta.dto;

import java.time.OffsetDateTime;

public record AnnouncementResponse(
        Long announcementId,
        String title,
        String message,
        OffsetDateTime createdAt,
        String adminName
) {}
