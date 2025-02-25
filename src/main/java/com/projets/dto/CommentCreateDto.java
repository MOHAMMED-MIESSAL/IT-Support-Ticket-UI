package com.projets.dto;

public class CommentCreateDto {
    private String commentText;
    private String ticketId;
    private String userId;

    // Constructeur
    public CommentCreateDto(String ticketId,String commentText, String userId) {
        this.ticketId = ticketId;
        this.commentText = commentText;
        this.userId = userId;
    }

    // Getters et setters
    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

