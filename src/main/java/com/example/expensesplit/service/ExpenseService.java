package com.example.expensesplit.service;

import com.example.expensesplit.dto.ExpenseDTO;
import com.example.expensesplit.dto.ExpenseParticipantDTO;
import com.example.expensesplit.entity.*;
import com.example.expensesplit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public List<ExpenseDTO> getAllExpenses() {
        try {
            return expenseRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error loading expenses: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        try {
            Group group = groupRepository.findById(expenseDTO.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found with id: " + expenseDTO.getGroupId()));
            
            User paidBy = userRepository.findById(expenseDTO.getPaidBy())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + expenseDTO.getPaidBy()));
            
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
            
            return convertToDTO(expenseRepository.findById(savedExpense.getExpenseId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve created expense")));
        } catch (Exception e) {
            throw new RuntimeException("Error creating expense: " + e.getMessage(), e);
        }
    }
    
    public List<ExpenseDTO> getExpensesByGroup(Long groupId) {
        try {
            if (!groupRepository.existsById(groupId)) {
                throw new RuntimeException("Group not found with id: " + groupId);
            }
            return expenseRepository.findByGroupGroupId(groupId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error loading expenses for group: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void addParticipantsToExpense(Long expenseId, List<ExpenseParticipantDTO> participantDTOs) {
        try {
            Expense expense = expenseRepository.findById(expenseId)
                    .orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));
            
            // Clear existing participants
            List<ExpenseParticipant> existingParticipants = expenseParticipantRepository.findByExpenseExpenseId(expenseId);
            expenseParticipantRepository.deleteAll(existingParticipants);
            
            Double totalShare = participantDTOs.stream()
                    .map(ExpenseParticipantDTO::getShareAmount)
                    .reduce(0.0, Double::sum);
            
            // Allow small rounding differences
            if (Math.abs(totalShare - expense.getAmount()) > 0.01) {
                throw new RuntimeException("Total share amount must equal expense amount. Expected: " + expense.getAmount() + ", Got: " + totalShare);
            }
            
            for (ExpenseParticipantDTO participantDTO : participantDTOs) {
                User user = userRepository.findById(participantDTO.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + participantDTO.getUserId()));
                
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
            
            // Refresh the expense
            expenseRepository.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error adding participants: " + e.getMessage(), e);
        }
    }
    
    public List<ExpenseParticipantDTO> getExpenseParticipants(Long expenseId) {
        try {
            if (!expenseRepository.existsById(expenseId)) {
                throw new RuntimeException("Expense not found with id: " + expenseId);
            }
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseExpenseId(expenseId);
            return participants.stream()
                    .map(this::convertParticipantToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error loading expense participants: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void deleteExpense(Long expenseId) {
        try {
            Expense expense = expenseRepository.findById(expenseId)
                    .orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));
            
            // Delete participants first
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseExpenseId(expenseId);
            expenseParticipantRepository.deleteAll(participants);
            
            // Then delete the expense
            expenseRepository.delete(expense);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting expense: " + e.getMessage(), e);
        }
    }
    
    private void splitEqually(Expense expense) {
        try {
            List<GroupMember> groupMembers = groupMemberRepository.findByGroupGroupId(expense.getGroup().getGroupId());
            int memberCount = groupMembers.size();
            
            if (memberCount == 0) {
                throw new RuntimeException("No members in group");
            }
            
            Double shareAmount = expense.getAmount() / memberCount;
            shareAmount = Math.round(shareAmount * 100.0) / 100.0; // Round to 2 decimal places
            
            // Handle rounding differences by adjusting the last share
            Double totalDistributed = 0.0;
            
            for (int i = 0; i < groupMembers.size(); i++) {
                GroupMember member = groupMembers.get(i);
                Double currentShare = shareAmount;
                
                // Adjust last share to account for rounding
                if (i == groupMembers.size() - 1) {
                    currentShare = expense.getAmount() - totalDistributed;
                } else {
                    totalDistributed += currentShare;
                }
                
                ExpenseParticipant participant = new ExpenseParticipant();
                participant.setExpense(expense);
                participant.setUser(member.getUser());
                participant.setShareAmount(currentShare);
                
                expenseParticipantRepository.save(participant);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error splitting expense equally: " + e.getMessage(), e);
        }
    }
    
    private ExpenseDTO convertToDTO(Expense expense) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Error converting expense to DTO: " + e.getMessage(), e);
        }
    }
    
    private ExpenseParticipantDTO convertParticipantToDTO(ExpenseParticipant participant) {
        ExpenseParticipantDTO dto = new ExpenseParticipantDTO();
        dto.setUserId(participant.getUser().getUserId());
        dto.setShareAmount(participant.getShareAmount());
        return dto;
    }
}