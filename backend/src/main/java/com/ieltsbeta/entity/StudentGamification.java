package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * One row per student. student_id is both the primary key AND the foreign
 * key to students -- no separate identity column, matching schema.sql.
 * No JPA relationship object to Student is mapped here (deliberately kept
 * as a plain Long): callers always already have the Student in context
 * when they touch this table, so navigation isn't needed.
 */
@Entity
@Table(name = "student_gamification")
public class StudentGamification {

    @Id
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "xp", nullable = false)
    private Integer xp = 0;

    @Column(name = "coins", nullable = false)
    private Integer coins = 0;

    @Column(name = "streak", nullable = false)
    private Integer streak = 0;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public StudentGamification() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
