package com.example.expensesplit.dto;

import java.math.BigDecimal;

public class BalanceDTO {
    private Long userId;
    private String userName;
    private BigDecimal balance;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
