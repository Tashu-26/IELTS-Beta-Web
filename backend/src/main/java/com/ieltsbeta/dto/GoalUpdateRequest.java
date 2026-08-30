package com.ieltsbeta.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/** Either field may be omitted (null) to leave that value unchanged. */
public record GoalUpdateRequest(
        @DecimalMin("0.0") @DecimalMax("9.0") BigDecimal targetBand,
        @DecimalMin("0.0") @DecimalMax("9.0") BigDecimal currentBand
) {}
