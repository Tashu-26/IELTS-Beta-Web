package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "test_results")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private TestAttempt attempt;

    @Column(name = "overall_band")
    private BigDecimal overallBand;

    @Column(name = "listening")
    private BigDecimal listening;

    @Column(name = "reading")
    private BigDecimal reading;

    @Column(name = "writing")
    private BigDecimal writing;

    @Column(name = "speaking")
    private BigDecimal speaking;

    @Column(name = "feedback")
    private String feedback;

    public TestResult() {
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public TestAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(TestAttempt attempt) {
        this.attempt = attempt;
    }

    public BigDecimal getOverallBand() {
        return overallBand;
    }

    public void setOverallBand(BigDecimal overallBand) {
        this.overallBand = overallBand;
    }

    public BigDecimal getListening() {
        return listening;
    }

    public void setListening(BigDecimal listening) {
        this.listening = listening;
    }

    public BigDecimal getReading() {
        return reading;
    }

    public void setReading(BigDecimal reading) {
        this.reading = reading;
    }

    public BigDecimal getWriting() {
        return writing;
    }

    public void setWriting(BigDecimal writing) {
        this.writing = writing;
    }

    public BigDecimal getSpeaking() {
        return speaking;
    }

    public void setSpeaking(BigDecimal speaking) {
        this.speaking = speaking;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
