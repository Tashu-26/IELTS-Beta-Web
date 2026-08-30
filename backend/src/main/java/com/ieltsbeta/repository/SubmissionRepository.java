package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent_StudentIdOrderBySubmittedAtDesc(Long studentId);
    List<Submission> findByCourse_CourseIdInOrderBySubmittedAtDesc(List<Long> courseIds);
    List<Submission> findAllByOrderBySubmittedAtDesc();
}
