package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Exactly one of textContent (skill="Writing") or audioUrl (skill="Speaking")
 * must be provided -- enforced in the service layer and again by the
 * database check constraint as a safety net.
 */
public record SubmissionCreateRequest(
        @NotNull Long courseId,
        Long practiceTestId,
        @NotBlank String skill,
        @NotBlank String submissionType,
        String textContent,
        String audioUrl
) {}
