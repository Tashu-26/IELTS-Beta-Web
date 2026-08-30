package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "student_missions")
public class StudentMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_mission_id")
    private Long studentMissionId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /** Matches a key in GamificationCatalog.MISSIONS (not a DB-stored catalog -- see that class). */
    @Column(name = "mission_key", nullable = false)
    private String missionKey;

    @Column(name = "mission_date", nullable = false)
    private LocalDate missionDate;

    @Column(name = "done", nullable = false)
    private Boolean done = false;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    public StudentMission() {
    }

    public Long getStudentMissionId() {
        return studentMissionId;
    }

    public void setStudentMissionId(Long studentMissionId) {
        this.studentMissionId = studentMissionId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getMissionKey() {
        return missionKey;
    }

    public void setMissionKey(String missionKey) {
        this.missionKey = missionKey;
    }

    public LocalDate getMissionDate() {
        return missionDate;
    }

    public void setMissionDate(LocalDate missionDate) {
        this.missionDate = missionDate;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
