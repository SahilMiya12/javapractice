package com.example.expensesplit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String contactNo;
    
    @Column(name = "join_date")
    private LocalDateTime joinDate = LocalDateTime.now();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMember> groupMemberships = new ArrayList<>();
    
    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expensesPaid = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpenseParticipant> expenseParticipants = new ArrayList<>();

    // Constructors
    public User() {}
    
    public User(String name, String email, String password, String contactNo) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNo = contactNo;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    
    public LocalDateTime getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
    
    public List<GroupMember> getGroupMemberships() { return groupMemberships; }
    public void setGroupMemberships(List<GroupMember> groupMemberships) { this.groupMemberships = groupMemberships; }
    
    public List<Expense> getExpensesPaid() { return expensesPaid; }
    public void setExpensesPaid(List<Expense> expensesPaid) { this.expensesPaid = expensesPaid; }
    
    public List<ExpenseParticipant> getExpenseParticipants() { return expenseParticipants; }
    public void setExpenseParticipants(List<ExpenseParticipant> expenseParticipants) { this.expenseParticipants = expenseParticipants; }
}