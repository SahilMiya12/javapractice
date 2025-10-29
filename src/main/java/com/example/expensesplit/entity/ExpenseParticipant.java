package com.example.expensesplit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_participants")
public class ExpenseParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;
    
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Double shareAmount;

    // Getters and Setters
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    
    public Expense getExpense() { return expense; }
    public void setExpense(Expense expense) { this.expense = expense; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Double getShareAmount() { return shareAmount; }
    public void setShareAmount(Double shareAmount) { this.shareAmount = shareAmount; }
}