package com.ieltsbeta.controller;

import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.dto.UpdateProfileRequest;
import com.ieltsbeta.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class MeController {

    private final AuthService authService;

    public MeController(AuthService authService) {
        this.authService = authService;
    }

    /** Returns 404 if /api/auth/complete-profile hasn't been called yet for this account. */
    @GetMapping("/api/me")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt) {
        return authService.getMe(UUID.fromString(jwt.getSubject()));
    }

    /**
     * Profile settings: edit the logged-in account's own first/last name.
     * Works for any role (Student, Teacher, Admin) since name lives on the
     * shared Person row, not a role-specific table.
     */
    @PutMapping("/api/me")
    public MeResponse updateMe(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateProfileRequest request) {
        return authService.updateProfile(UUID.fromString(jwt.getSubject()), request);
    }
}
