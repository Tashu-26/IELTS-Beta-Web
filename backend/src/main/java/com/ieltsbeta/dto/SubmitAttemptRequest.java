package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitAttemptRequest(
        @NotNull List<AnswerSubmissionDto> answers
) {}
