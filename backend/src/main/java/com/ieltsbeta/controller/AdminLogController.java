package com.ieltsbeta.controller;

import com.ieltsbeta.dto.AdminLogResponse;
import com.ieltsbeta.service.AdminLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminLogController {

    private final AdminLogService adminLogService;

    public AdminLogController(AdminLogService adminLogService) {
        this.adminLogService = adminLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminLogResponse> list() {
        return adminLogService.listAll();
    }
}
