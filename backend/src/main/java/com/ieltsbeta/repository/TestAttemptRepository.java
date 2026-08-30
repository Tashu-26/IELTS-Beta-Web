package com.ieltsbeta.repository;

import com.ieltsbeta.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findByStudent_StudentIdOrderByStartTimeDesc(Long studentId);
}
