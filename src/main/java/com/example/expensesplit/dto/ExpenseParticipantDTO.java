package com.example.expensesplit.dto;

public class ExpenseParticipantDTO {
    private Long userId;
    private Double shareAmount;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Double getShareAmount() { return shareAmount; }
    public void setShareAmount(Double shareAmount) { this.shareAmount = shareAmount; }
}