package com.ieltsbeta.controller;

import com.ieltsbeta.dto.AdminReportResponse;
import com.ieltsbeta.dto.AdminUserResponse;
import com.ieltsbeta.dto.UserRoleUpdateRequest;
import com.ieltsbeta.dto.UserStatusUpdateRequest;
import com.ieltsbeta.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> listUsers() {
        return adminUserService.listUsers();
    }

    @PutMapping("/users/{userId}/role")
    public AdminUserResponse updateRole(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable Long userId,
                                         @Valid @RequestBody UserRoleUpdateRequest request) {
        return adminUserService.updateRole(jwt, userId, request);
    }

    @PutMapping("/users/{userId}/status")
    public AdminUserResponse updateStatus(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable Long userId,
                                           @Valid @RequestBody UserStatusUpdateRequest request) {
        return adminUserService.updateStatus(jwt, userId, request);
    }

    @GetMapping("/reports")
    public AdminReportResponse report() {
        return adminUserService.getReport();
    }
}
