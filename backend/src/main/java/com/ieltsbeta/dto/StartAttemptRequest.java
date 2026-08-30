package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotNull;

public record StartAttemptRequest(
        @NotNull Long testId
) {}
