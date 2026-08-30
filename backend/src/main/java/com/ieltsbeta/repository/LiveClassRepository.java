package com.ieltsbeta.repository;

import com.ieltsbeta.entity.LiveClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveClassRepository extends JpaRepository<LiveClass, Long> {
    List<LiveClass> findByTeacherCourse_Course_CourseId(Long courseId);
    List<LiveClass> findByTeacherCourse_Teacher_TeacherId(Long teacherId);
}
