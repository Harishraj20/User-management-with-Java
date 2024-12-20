package com.task.Model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {

    private int userId;

    private String userName;
    private String password;
    private String emailId;
    private String dob;
    private String designation;
    private String role;
    private String gender;
    private int loginStatus = 0;

    @JsonIgnore
    private Set<Login> logins;

    public User() {
    }

    public User(String userName, String password, String emailId, String dob, String designation, String role,
            int loginStatus, String gender) {
        this.userName = userName;
        this.password = password;
        this.emailId = emailId;
        this.dob = dob;
        this.designation = designation;
        this.role = role;
        this.loginStatus = loginStatus;
    }

    public String getEmployeeId() {
        return "E2E50" + String.format("%02d", userId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Set<Login> getLogins() {
        return logins;
    }

    public void setLogins(Set<Login> logins) {
        this.logins = logins;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", userName=" + userName + ", password=" + password + ", emailId=" + emailId
                + ", dob=" + dob + ", designation=" + designation + ", role=" + role + ", gender=" + gender
                + ", loginStatus=" + loginStatus + "]";
    }
}
