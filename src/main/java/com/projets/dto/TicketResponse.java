package com.projets.dto;

import java.util.List;
import java.util.Map;

public class TicketResponse {
    private List<Map<String, Object>> content;

    // Getters et setters
    public List<Map<String, Object>> getContent() {
        return content;
    }

    public void setContent(List<Map<String, Object>> content) {
        this.content = content;
    }
}