package com.ieltsbeta.controller;

import com.ieltsbeta.dto.GoalUpdateRequest;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/students/me")
public class StudentController {

    private final AuthService authService;

    public StudentController(AuthService authService) {
        this.authService = authService;
    }

    @PutMapping("/goal")
    @PreAuthorize("hasRole('STUDENT')")
    public MeResponse updateGoal(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody GoalUpdateRequest request) {
        return authService.updateGoal(UUID.fromString(jwt.getSubject()), request);
    }
}
