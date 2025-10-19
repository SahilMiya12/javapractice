package com.example.expensesplit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 50)
    private String name;
    
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(length = 20)
    private String contactNo;
    
    @CreationTimestamp
    @Column(name = "join_date")
    private LocalDateTime joinDate;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<GroupMember> groupMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "paidBy")
    private List<Expense> expensesPaid = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ExpenseParticipant> expenseParticipants = new ArrayList<>();

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    
    public LocalDateTime getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
    
    public List<GroupMember> getGroupMembers() { return groupMembers; }
    public void setGroupMembers(List<GroupMember> groupMembers) { this.groupMembers = groupMembers; }
    
    public List<Expense> getExpensesPaid() { return expensesPaid; }
    public void setExpensesPaid(List<Expense> expensesPaid) { this.expensesPaid = expensesPaid; }
    
    public List<ExpenseParticipant> getExpenseParticipants() { return expenseParticipants; }
    public void setExpenseParticipants(List<ExpenseParticipant> expenseParticipants) { this.expenseParticipants = expenseParticipants; }
}


