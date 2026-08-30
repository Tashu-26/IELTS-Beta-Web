package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

/** role must be one of: Student, Teacher, Admin. */
public record RoleUpdateRequest(
        @NotBlank String role
) {}
