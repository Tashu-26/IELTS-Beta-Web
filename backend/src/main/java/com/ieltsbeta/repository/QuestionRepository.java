package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTest_TestId(Long testId);
}
