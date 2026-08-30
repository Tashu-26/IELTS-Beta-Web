package com.ieltsbeta.controller;

import com.ieltsbeta.dto.TeacherCourseResponse;
import com.ieltsbeta.service.TeacherCourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeacherCourseController {

    private final TeacherCourseService teacherCourseService;

    public TeacherCourseController(TeacherCourseService teacherCourseService) {
        this.teacherCourseService = teacherCourseService;
    }

    @GetMapping("/api/teachers/me/courses")
    @PreAuthorize("hasRole('TEACHER')")
    public List<TeacherCourseResponse> myCourses(@AuthenticationPrincipal Jwt jwt) {
        return teacherCourseService.myCourses(jwt);
    }

    @PostMapping("/api/courses/{courseId}/teachers/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public TeacherCourseResponse assignTeacher(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long courseId,
                                                @PathVariable Long teacherId) {
        return teacherCourseService.assignTeacher(jwt, courseId, teacherId);
    }
}
