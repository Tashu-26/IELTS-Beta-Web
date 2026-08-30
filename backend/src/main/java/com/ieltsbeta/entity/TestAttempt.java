package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "test_attempts")
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private PracticeTest test;

    @Column(name = "start_time", insertable = false, updatable = false)
    private OffsetDateTime startTime;

    @Column(name = "submit_time")
    private OffsetDateTime submitTime;

    @Column(name = "score")
    private BigDecimal score;

    @Column(name = "band_score")
    private BigDecimal bandScore;

    public TestAttempt() {
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public PracticeTest getTest() {
        return test;
    }

    public void setTest(PracticeTest test) {
        this.test = test;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public OffsetDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(OffsetDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getBandScore() {
        return bandScore;
    }

    public void setBandScore(BigDecimal bandScore) {
        this.bandScore = bandScore;
    }
}
