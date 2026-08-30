package com.ieltsbeta.repository;

import com.ieltsbeta.entity.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    List<TeacherCourse> findByTeacher_TeacherId(Long teacherId);
    Optional<TeacherCourse> findByTeacher_TeacherIdAndCourse_CourseId(Long teacherId, Long courseId);
    boolean existsByTeacher_TeacherIdAndCourse_CourseId(Long teacherId, Long courseId);
}
