package com.ieltsbeta.controller;

import com.ieltsbeta.dto.AnnouncementCreateRequest;
import com.ieltsbeta.dto.AnnouncementResponse;
import com.ieltsbeta.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public List<AnnouncementResponse> list() {
        return announcementService.listAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AnnouncementResponse create(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody AnnouncementCreateRequest request) {
        return announcementService.create(jwt, request);
    }
}
