package com.ieltsbeta.controller;

import com.ieltsbeta.dto.CourseCreateRequest;
import com.ieltsbeta.dto.CourseResponse;
import com.ieltsbeta.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponse> list() {
        return courseService.listCourses();
    }

    @GetMapping("/{courseId}")
    public CourseResponse get(@PathVariable Long courseId) {
        return courseService.getCourse(courseId);
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public CourseResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CourseCreateRequest request) {
        return courseService.createCourse(jwt, request);
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public CourseResponse update(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable Long courseId,
                                  @Valid @RequestBody CourseCreateRequest request) {
        return courseService.updateCourse(jwt, courseId, request);
    }
}
