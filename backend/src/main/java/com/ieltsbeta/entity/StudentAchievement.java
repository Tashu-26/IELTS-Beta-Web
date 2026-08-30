package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "student_achievements")
public class StudentAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_achievement_id")
    private Long studentAchievementId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /** Matches a key in GamificationCatalog.ACHIEVEMENTS. */
    @Column(name = "achievement_key", nullable = false)
    private String achievementKey;

    @Column(name = "unlocked_at", insertable = false, updatable = false)
    private OffsetDateTime unlockedAt;

    public StudentAchievement() {
    }

    public Long getStudentAchievementId() {
        return studentAchievementId;
    }

    public void setStudentAchievementId(Long studentAchievementId) {
        this.studentAchievementId = studentAchievementId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getAchievementKey() {
        return achievementKey;
    }

    public void setAchievementKey(String achievementKey) {
        this.achievementKey = achievementKey;
    }

    public OffsetDateTime getUnlockedAt() {
        return unlockedAt;
    }
}
