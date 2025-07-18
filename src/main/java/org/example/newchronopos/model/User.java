package org.example.newchronopos.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class User {
    private int id;
    private String fullName;
    private String username; // Added
    private String email;
    private String password;
    private String role;
    private String phoneNo;
    private double salary;
    private Date dob;
    private boolean changeAccess;
    private Time shiftStartTime;
    private Time shiftEndTime;
    private String address;
    private String additionalDetails;
    private String uaeId;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String fullName, String username, String email, String password, String role,
                String phoneNo, double salary, Date dob, boolean changeAccess,
                Time shiftStartTime, Time shiftEndTime, String address,
                String additionalDetails, String uaeId, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNo = phoneNo;
        this.salary = salary;
        this.dob = dob;
        this.changeAccess = changeAccess;
        this.shiftStartTime = shiftStartTime;
        this.shiftEndTime = shiftEndTime;
        this.address = address;
        this.additionalDetails = additionalDetails;
        this.uaeId = uaeId;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public boolean isChangeAccess() {
        return changeAccess;
    }

    public void setChangeAccess(boolean changeAccess) {
        this.changeAccess = changeAccess;
    }

    public Time getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(Time shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public Time getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(Time shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public String getUaeId() {
        return uaeId;
    }

    public void setUaeId(String uaeId) {
        this.uaeId = uaeId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
