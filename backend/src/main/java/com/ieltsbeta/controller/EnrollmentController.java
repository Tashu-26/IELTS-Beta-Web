package com.ieltsbeta.controller;

import com.ieltsbeta.dto.CourseEnrollmentResponse;
import com.ieltsbeta.dto.EnrollRequest;
import com.ieltsbeta.dto.EnrollmentResponse;
import com.ieltsbeta.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/api/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public EnrollmentResponse enroll(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody EnrollRequest request) {
        return enrollmentService.enroll(jwt, request);
    }

    @GetMapping("/api/students/me/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public List<EnrollmentResponse> myEnrollments(@AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.myEnrollments(jwt);
    }

    @GetMapping("/api/courses/{courseId}/enrollments")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public List<CourseEnrollmentResponse> listForCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable Long courseId) {
        return enrollmentService.listForCourse(jwt, courseId);
    }
}
