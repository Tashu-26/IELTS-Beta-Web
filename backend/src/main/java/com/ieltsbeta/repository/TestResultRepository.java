package com.ieltsbeta.repository;

import com.ieltsbeta.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Optional<TestResult> findByAttempt_AttemptId(Long attemptId);
    List<TestResult> findByAttempt_Student_StudentId(Long studentId);
}
