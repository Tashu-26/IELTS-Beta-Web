package com.ieltsbeta.controller;

import com.ieltsbeta.dto.*;
import com.ieltsbeta.service.TestAttemptService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('STUDENT')")
public class TestAttemptController {

    private final TestAttemptService testAttemptService;

    public TestAttemptController(TestAttemptService testAttemptService) {
        this.testAttemptService = testAttemptService;
    }

    @PostMapping("/api/test-attempts")
    public AttemptStartResponse start(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody StartAttemptRequest request) {
        return testAttemptService.startAttempt(jwt, request);
    }

    @PutMapping("/api/test-attempts/{attemptId}/submit")
    public AttemptResultResponse submit(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable Long attemptId,
                                         @Valid @RequestBody SubmitAttemptRequest request) {
        return testAttemptService.submitAttempt(jwt, attemptId, request);
    }

    @GetMapping("/api/test-attempts/{attemptId}")
    public AttemptResultResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long attemptId) {
        return testAttemptService.getAttempt(jwt, attemptId);
    }

    @GetMapping("/api/students/me/test-attempts")
    public List<AttemptSummaryResponse> myAttempts(@AuthenticationPrincipal Jwt jwt) {
        return testAttemptService.listMyAttempts(jwt);
    }

    @GetMapping("/api/students/me/progress")
    public ProgressResponse progress(@AuthenticationPrincipal Jwt jwt) {
        return testAttemptService.getProgress(jwt);
    }
}
