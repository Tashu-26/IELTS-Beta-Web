package com.ieltsbeta.controller;

import com.ieltsbeta.dto.GamificationSummaryResponse;
import com.ieltsbeta.service.GamificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GamificationController {

    private final GamificationService gamificationService;

    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @GetMapping("/api/students/me/gamification")
    @PreAuthorize("hasRole('STUDENT')")
    public GamificationSummaryResponse summary(@AuthenticationPrincipal Jwt jwt) {
        return gamificationService.getSummary(jwt);
    }
}
