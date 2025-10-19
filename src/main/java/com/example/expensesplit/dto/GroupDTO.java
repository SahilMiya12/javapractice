package com.example.expensesplit.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class GroupDTO {
    private Long groupId;
    
    @NotBlank(message = "Group name is required")
    private String name;
    
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
