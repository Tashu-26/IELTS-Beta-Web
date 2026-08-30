package com.ieltsbeta.repository;

import com.ieltsbeta.entity.PracticeTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeTestRepository extends JpaRepository<PracticeTest, Long> {
    List<PracticeTest> findByCourse_CourseId(Long courseId);
}
