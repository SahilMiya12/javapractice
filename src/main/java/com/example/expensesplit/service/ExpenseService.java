package com.example.expensesplit.service;

import com.example.expensesplit.dto.ExpenseDTO;
import com.example.expensesplit.dto.ExpenseParticipantDTO;
import com.example.expensesplit.entity.*;
import com.example.expensesplit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;
    
    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        Group group = groupRepository.findById(expenseDTO.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User paidBy = userRepository.findById(expenseDTO.getPaidBy())
                .orElseThrow(() -> new RuntimeException("Payer not found"));
        
        // Verify payer is a group member
        if (!groupMemberRepository.existsByGroupGroupIdAndUserUserId(expenseDTO.getGroupId(), expenseDTO.getPaidBy())) {
            throw new RuntimeException("Payer is not a member of this group");
        }
        
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        
        Expense savedExpense = expenseRepository.save(expense);
        
        // Add participants if provided
        if (expenseDTO.getParticipants() != null && !expenseDTO.getParticipants().isEmpty()) {
            addParticipantsToExpense(savedExpense.getExpenseId(), expenseDTO.getParticipants());
        } else {
            // Default: split equally among all group members
            splitEqually(savedExpense);
        }
        
        return convertToDTO(savedExpense);
    }
    
    public List<ExpenseDTO> getExpensesByGroup(Long groupId) {
        return expenseRepository.findByGroupGroupId(groupId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void addParticipantsToExpense(Long expenseId, List<ExpenseParticipantDTO> participantDTOs) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        // Clear existing participants
        expenseParticipantRepository.deleteAll(expense.getParticipants());
        expense.getParticipants().clear();
        
        BigDecimal totalShare = participantDTOs.stream()
                .map(ExpenseParticipantDTO::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalShare.compareTo(expense.getAmount()) != 0) {
            throw new RuntimeException("Total share amount must equal expense amount");
        }
        
        for (ExpenseParticipantDTO participantDTO : participantDTOs) {
            User user = userRepository.findById(participantDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Verify user is a group member
            if (!groupMemberRepository.existsByGroupGroupIdAndUserUserId(
                    expense.getGroup().getGroupId(), participantDTO.getUserId())) {
                throw new RuntimeException("User is not a member of this group");
            }
            
            ExpenseParticipant participant = new ExpenseParticipant();
            participant.setExpense(expense);
            participant.setUser(user);
            participant.setShareAmount(participantDTO.getShareAmount());
            
            expenseParticipantRepository.save(participant);
        }
    }
    
    public List<ExpenseParticipantDTO> getExpenseParticipants(Long expenseId) {
        List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseExpenseId(expenseId);
        return participants.stream()
                .map(this::convertParticipantToDTO)
                .collect(Collectors.toList());
    }
    
    // ADD THIS METHOD
    @Transactional
    public void deleteExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expenseRepository.delete(expense);
    }
    
    private void splitEqually(Expense expense) {
        List<GroupMember> groupMembers = groupMemberRepository.findByGroupGroupId(expense.getGroup().getGroupId());
        int memberCount = groupMembers.size();
        
        if (memberCount == 0) {
            throw new RuntimeException("No members in group");
        }
        
        BigDecimal shareAmount = expense.getAmount()
                .divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP);
        
        for (GroupMember member : groupMembers) {
            ExpenseParticipant participant = new ExpenseParticipant();
            participant.setExpense(expense);
            participant.setUser(member.getUser());
            participant.setShareAmount(shareAmount);
            
            expenseParticipantRepository.save(participant);
        }
    }
    
    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setExpenseId(expense.getExpenseId());
        dto.setGroupId(expense.getGroup().getGroupId());
        dto.setPaidBy(expense.getPaidBy().getUserId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setExpenseDate(expense.getExpenseDate());
        
        List<ExpenseParticipantDTO> participants = expense.getParticipants().stream()
                .map(this::convertParticipantToDTO)
                .collect(Collectors.toList());
        dto.setParticipants(participants);
        
        return dto;
    }
    
    private ExpenseParticipantDTO convertParticipantToDTO(ExpenseParticipant participant) {
        ExpenseParticipantDTO dto = new ExpenseParticipantDTO();
        dto.setUserId(participant.getUser().getUserId());
        dto.setShareAmount(participant.getShareAmount());
        return dto;
    }
}