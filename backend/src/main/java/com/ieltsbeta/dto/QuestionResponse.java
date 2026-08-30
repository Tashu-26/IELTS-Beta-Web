package com.ieltsbeta.dto;

import java.util.List;

public record QuestionResponse(
        Long questionId,
        String questionText,
        String skill,
        Integer marks,
        List<AnswerOptionResponse> options
) {}
