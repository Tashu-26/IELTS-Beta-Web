package com.ieltsbeta.controller;

import com.ieltsbeta.dto.ContentCreateRequest;
import com.ieltsbeta.dto.ContentResponse;
import com.ieltsbeta.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public List<ContentResponse> list(@PathVariable Long courseId) {
        return contentService.listForCourse(courseId);
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ContentResponse create(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable Long courseId,
                                   @Valid @RequestBody ContentCreateRequest request) {
        return contentService.create(jwt, courseId, request);
    }
}
