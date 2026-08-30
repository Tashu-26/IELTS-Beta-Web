package com.ieltsbeta.dto;

public record AdminReportResponse(
        long totalUsers,
        long totalStudents,
        long totalTeachers,
        long totalCourses,
        long totalEnrollments,
        long openSupportTickets
) {}
