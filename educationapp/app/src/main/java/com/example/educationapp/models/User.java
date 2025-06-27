package com.example.educationapp.models;

import java.util.List;

public class User {
    private String userId;
    private String email;
    private String fullName;  // Changed from 'name' to match fragment usage
    private String role; // "admin", "teacher", "student"
    private String phoneNumber;
    private List<String> assignedCourses; // For students and teachers
    private String teacherId; // For students - which teacher they're assigned to
    private long createdAt;

    public User() {
        // Required empty constructor for Firestore
        this.createdAt = System.currentTimeMillis();
    }

    public User(String userId, String email, String fullName, String role, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Keep this for backward compatibility
    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getAssignedCourses() {
        return assignedCourses;
    }

    public void setAssignedCourses(List<String> assignedCourses) {
        this.assignedCourses = assignedCourses;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}