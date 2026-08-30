package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent_StudentId(Long studentId);
    List<Enrollment> findByCourse_CourseId(Long courseId);
    Optional<Enrollment> findByStudent_StudentIdAndCourse_CourseId(Long studentId, Long courseId);
    boolean existsByStudent_StudentIdAndCourse_CourseId(Long studentId, Long courseId);
}
