package com.ieltsbeta.controller;

import com.ieltsbeta.dto.CompleteProfileRequest;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Called the first time an authenticated Supabase session reaches the
     * backend (right after signup, or after email confirmation + first
     * login). The Authorization header carries the verified Supabase JWT;
     * we read the subject (auth user id) and email from it, never from the
     * request body. Idempotent -- safe to call more than once.
     */
    @PostMapping("/complete-profile")
    public MeResponse completeProfile(@AuthenticationPrincipal Jwt jwt,
                                       @Valid @RequestBody CompleteProfileRequest request) {
        UUID authUserId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        return authService.completeProfile(authUserId, email, request);
    }
}
