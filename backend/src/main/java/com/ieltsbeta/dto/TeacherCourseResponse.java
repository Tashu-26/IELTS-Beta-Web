package com.ieltsbeta.dto;

public record TeacherCourseResponse(
        Long teacherCourseId,
        Long courseId,
        String courseTitle,
        Long teacherId,
        String teacherName,
        Boolean isActive
) {}
