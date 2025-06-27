package com.example.educationapp.models;

import java.util.List;

public class Course {
    private String courseId;
    private String courseName;
    private String courseCode;
    private String description;  // Changed from courseDescription
    private String teacherId;
    private String teacherName;
    private List<String> enrolledStudents;
    private List<String> materials; // List of material IDs
    private long createdAt;

    public Course() {
        // Required empty constructor for Firestore
    }

    public Course(String courseId, String courseName, String courseCode, String description,
                  String teacherId, String teacherName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.description = description;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Keep this for backward compatibility if needed
    public String getCourseDescription() {
        return description;
    }

    public void setCourseDescription(String description) {
        this.description = description;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public List<String> getMaterials() {
        return materials;
    }

    public void setMaterials(List<String> materials) {
        this.materials = materials;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}