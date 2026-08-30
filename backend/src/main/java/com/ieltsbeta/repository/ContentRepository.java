package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCourse_CourseId(Long courseId);
}
