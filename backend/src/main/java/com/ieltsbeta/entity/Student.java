package com.ieltsbeta.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "target_band")
    private BigDecimal targetBand;

    @Column(name = "current_band")
    private BigDecimal currentBand;

    @Column(name = "days_active", nullable = false)
    private Integer daysActive = 0;

    public Student() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public BigDecimal getTargetBand() {
        return targetBand;
    }

    public void setTargetBand(BigDecimal targetBand) {
        this.targetBand = targetBand;
    }

    public BigDecimal getCurrentBand() {
        return currentBand;
    }

    public void setCurrentBand(BigDecimal currentBand) {
        this.currentBand = currentBand;
    }

    public Integer getDaysActive() {
        return daysActive;
    }

    public void setDaysActive(Integer daysActive) {
        this.daysActive = daysActive;
    }
}
