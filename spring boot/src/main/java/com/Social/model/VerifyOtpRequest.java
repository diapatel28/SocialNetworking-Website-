package com.Social.model;

public class VerifyOtpRequest {
    private String email;
    private String otp;

    // Default constructor
    public VerifyOtpRequest() {
    }

    // Parameterized constructor
    public VerifyOtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
