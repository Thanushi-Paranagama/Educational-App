package com.example.educationapp.models;

public class CourseMaterial {
    private String materialId;
    private String courseId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private String fileType; // "pdf", "doc", "video", "image", etc.
    private String uploadedBy; // Teacher ID
    private long uploadedAt;
    private long fileSize;

    public CourseMaterial() {
        // Required empty constructor for Firestore
    }

    public CourseMaterial(String materialId, String courseId, String title,
                          String description, String fileUrl, String fileName,
                          String fileType, String uploadedBy) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public long getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}