package com.ieltsbeta.repository;

import com.ieltsbeta.entity.StudentAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAchievementRepository extends JpaRepository<StudentAchievement, Long> {
    boolean existsByStudentIdAndAchievementKey(Long studentId, String achievementKey);
    List<StudentAchievement> findByStudentId(Long studentId);
}
