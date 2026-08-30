package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

/** role must be one of: Student, Teacher, Admin. */
public record UserRoleUpdateRequest(
        @NotBlank String role
) {}
