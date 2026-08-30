package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

/** Used by PUT /api/students/me/profile to edit the logged-in user's name. */
public record UpdateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName
) {}
