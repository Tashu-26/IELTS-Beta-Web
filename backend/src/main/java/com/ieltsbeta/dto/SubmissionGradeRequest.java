package com.ieltsbeta.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SubmissionGradeRequest(
        @NotNull @DecimalMin("0.0") @DecimalMax("9.0") BigDecimal bandScore,
        @NotBlank String feedback
) {}
