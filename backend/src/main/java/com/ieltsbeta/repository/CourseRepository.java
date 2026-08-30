package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
