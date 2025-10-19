package com.example.expensesplit.dto;

import java.math.BigDecimal;

public class ExpenseParticipantDTO {
    private Long userId;
    private BigDecimal shareAmount;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public BigDecimal getShareAmount() { return shareAmount; }
    public void setShareAmount(BigDecimal shareAmount) { this.shareAmount = shareAmount; }
}

