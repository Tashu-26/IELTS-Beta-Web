package com.ieltsbeta.dto;

import jakarta.validation.constraints.NotBlank;

public class CompleteProfileRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    public CompleteProfileRequest() {
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
}
