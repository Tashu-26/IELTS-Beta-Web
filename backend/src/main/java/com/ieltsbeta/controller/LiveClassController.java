package com.ieltsbeta.controller;

import com.ieltsbeta.dto.LiveClassCreateRequest;
import com.ieltsbeta.dto.LiveClassResponse;
import com.ieltsbeta.service.LiveClassService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LiveClassController {

    private final LiveClassService liveClassService;

    public LiveClassController(LiveClassService liveClassService) {
        this.liveClassService = liveClassService;
    }

    @GetMapping("/api/courses/{courseId}/live-classes")
    public List<LiveClassResponse> listForCourse(@PathVariable Long courseId) {
        return liveClassService.listForCourse(courseId);
    }

    @PostMapping("/api/teacher-courses/{teacherCourseId}/live-classes")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public LiveClassResponse create(@AuthenticationPrincipal Jwt jwt,
                                     @PathVariable Long teacherCourseId,
                                     @Valid @RequestBody LiveClassCreateRequest request) {
        return liveClassService.create(jwt, teacherCourseId, request);
    }
}
