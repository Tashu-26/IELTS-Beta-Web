package com.ieltsbeta.dto;

import java.math.BigDecimal;

public class MeResponse {

    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private Long studentId;
    private BigDecimal targetBand;
    private BigDecimal currentBand;
    private Long teacherId;
    private String specialization;
    private Long adminId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
