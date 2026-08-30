package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_test_id")
    private PracticeTest practiceTest;

    /** One of: Writing, Speaking. */
    @Column(name = "skill", nullable = false)
    private String skill;

    /** e.g. "Task 1", "Task 2", "Part 2". */
    @Column(name = "submission_type", nullable = false)
    private String submissionType;

    @Column(name = "text_content")
    private String textContent;

    @Column(name = "audio_url")
    private String audioUrl;

    /** One of: Pending, Graded. */
    @Column(name = "status", nullable = false)
    private String status = "Pending";

    @Column(name = "band_score")
    private BigDecimal bandScore;

    @Column(name = "feedback")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private AppUser gradedBy;

    @Column(name = "submitted_at", insertable = false, updatable = false)
    private OffsetDateTime submittedAt;

    @Column(name = "graded_at")
    private OffsetDateTime gradedAt;

    public Submission() {
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public PracticeTest getPracticeTest() {
        return practiceTest;
    }

    public void setPracticeTest(PracticeTest practiceTest) {
        this.practiceTest = practiceTest;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBandScore() {
        return bandScore;
    }

    public void setBandScore(BigDecimal bandScore) {
        this.bandScore = bandScore;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public AppUser getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(AppUser gradedBy) {
        this.gradedBy = gradedBy;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public OffsetDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(OffsetDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }
}
