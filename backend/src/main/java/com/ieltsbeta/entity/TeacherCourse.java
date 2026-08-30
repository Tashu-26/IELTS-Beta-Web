package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "teacher_courses")
public class TeacherCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_course_id")
    private Long teacherCourseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "assigned_at", insertable = false, updatable = false)
    private OffsetDateTime assignedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public TeacherCourse() {
    }

    public Long getTeacherCourseId() {
        return teacherCourseId;
    }

    public void setTeacherCourseId(Long teacherCourseId) {
        this.teacherCourseId = teacherCourseId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public OffsetDateTime getAssignedAt() {
        return assignedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
