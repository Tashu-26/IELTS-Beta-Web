package com.ieltsbeta.controller;

import com.ieltsbeta.dto.SubmissionCreateRequest;
import com.ieltsbeta.dto.SubmissionGradeRequest;
import com.ieltsbeta.dto.SubmissionResponse;
import com.ieltsbeta.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/api/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public SubmissionResponse create(@AuthenticationPrincipal Jwt jwt,
                                      @Valid @RequestBody SubmissionCreateRequest request) {
        return submissionService.create(jwt, request);
    }

    @GetMapping("/api/students/me/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public List<SubmissionResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return submissionService.myList(jwt);
    }

    @GetMapping("/api/teachers/me/submissions")
    @PreAuthorize("hasRole('TEACHER')")
    public List<SubmissionResponse> teacherQueue(@AuthenticationPrincipal Jwt jwt) {
        return submissionService.teacherQueue(jwt);
    }

    @GetMapping("/api/submissions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SubmissionResponse> listAll(@AuthenticationPrincipal Jwt jwt) {
        return submissionService.listAll(jwt);
    }

    @PutMapping("/api/submissions/{submissionId}/grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public SubmissionResponse grade(@AuthenticationPrincipal Jwt jwt,
                                     @PathVariable Long submissionId,
                                     @Valid @RequestBody SubmissionGradeRequest request) {
        return submissionService.grade(jwt, submissionId, request);
    }
}
