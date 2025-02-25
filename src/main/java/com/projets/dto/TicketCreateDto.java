package com.projets.dto;

public class TicketCreateDto {
    private String title;
    private String description;
    private String priority;
    private String category;
    private String userId;

    public TicketCreateDto(String title, String description, String priority, String category, String userId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.userId = userId;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }

    public String getUserId() {
        return userId;
    }

    // Setters (optionnels)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
