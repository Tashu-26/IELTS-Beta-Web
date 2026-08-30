package com.ieltsbeta.controller;

import com.ieltsbeta.dto.*;
import com.ieltsbeta.service.PracticeTestService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PracticeTestController {

    private final PracticeTestService practiceTestService;

    public PracticeTestController(PracticeTestService practiceTestService) {
        this.practiceTestService = practiceTestService;
    }

    @GetMapping("/api/courses/{courseId}/practice-tests")
    public List<PracticeTestSummaryResponse> listForCourse(@PathVariable Long courseId) {
        return practiceTestService.listForCourse(courseId);
    }

    @GetMapping("/api/practice-tests/{testId}")
    public PracticeTestDetailResponse getDetail(@AuthenticationPrincipal Jwt jwt, @PathVariable Long testId) {
        return practiceTestService.getDetail(jwt, testId);
    }

    @PostMapping("/api/practice-tests")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public PracticeTestSummaryResponse create(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody PracticeTestCreateRequest request) {
        return practiceTestService.create(jwt, request);
    }

    @PostMapping("/api/practice-tests/{testId}/questions")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public QuestionResponse addQuestion(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable Long testId,
                                         @Valid @RequestBody QuestionCreateRequest request) {
        return practiceTestService.addQuestion(jwt, testId, request);
    }

    @PostMapping("/api/questions/{questionId}/answer-options")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public AnswerOptionResponse addAnswerOption(@AuthenticationPrincipal Jwt jwt,
                                                 @PathVariable Long questionId,
                                                 @Valid @RequestBody AnswerOptionCreateRequest request) {
        return practiceTestService.addAnswerOption(jwt, questionId, request);
    }
}
